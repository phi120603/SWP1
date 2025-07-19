package com.example.swp.controller.website;

import com.example.swp.dto.LoginRequest;
import com.example.swp.entity.Customer;
import com.example.swp.security.MyUserDetail;
import com.example.swp.service.CustomerService;
import com.example.swp.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class LoginRestController {

    @Autowired
    private HttpSession session;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerService customerService;


    @GetMapping({"/login", "/api/login"})
    public String returnLoginPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home-page";
        }
        model.addAttribute("sessionId", session.getId());
        return "login";

}


    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Tài khoản hoặc mật khẩu không chính xác.");
            }

            // Ghi nhận thông tin đăng nhập vào Spring Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Lưu thông tin vào session
            session.setMaxInactiveInterval(6000); // 10 phút
            session.setAttribute("email", loginRequest.getEmail());
            Customer customer = customerService.findByEmail(loginRequest.getEmail());
            if (customer != null) {
                session.setAttribute("loggedInCustomer", customer);
            }



            // Gắn context vào session
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());


            // Xác định vai trò để redirect
            String redirectUrl = "/home-page"; // default
            for (GrantedAuthority authority : authentication.getAuthorities()) {

                String role = authority.getAuthority();
                switch (role) {
                    case "MANAGER":
                        redirectUrl = "/admin/manager-dashboard";
                        break;
                    case "STAFF":
                        redirectUrl = "/staff/dashboard";
                        break;
                    case "CUSTOMER":
                        redirectUrl = "/home-page";
                        break;
                }
                break; // chỉ lấy role đầu tiên
            }

            Map<String, String> response = new HashMap<>();
            response.put("redirect", redirectUrl);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email hoặc mật khẩu không chính xác.");
        } catch (Exception e) {
            e.printStackTrace(); // in lỗi cho debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi đăng nhập.");
        }
    }


    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/api/login"; // Chuyển về trang login
    }


    @GetMapping("/check-session")
    @ResponseBody
    public ResponseEntity<String> checkSession() {
        Object email = session.getAttribute("email");
        if (email != null) {
            return ResponseEntity.ok("Đang đăng nhập với email: " + email);
        } else {
            return ResponseEntity.ok("Chưa đăng nhập hoặc session đã hết hạn.");
        }
    }
}
