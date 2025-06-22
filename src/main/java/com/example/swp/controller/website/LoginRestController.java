package com.example.swp.controller.website;
import com.example.swp.dto.LoginRequest;
import com.example.swp.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class LoginRestController {
    @Autowired
    private HttpSession session;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String returnlogin (Model model) {
        //lay ra thong tin gnuoi dung trong session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            // Nếu người dùng đã đăng nhập, chuyển hướng đến trang chủ

            return "redirect:/home-page"; // Chuyển hướng đến trang chủ
        }
        //kiem tra thong tin nguoi dung trong session
        String sessionId = session.getId();
        model.addAttribute("sessionId", sessionId);
        return "redirect:/login.html"; // Spring tìm file view tên là "login"
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            // xac thuc người dùng với thông tin đăng nhập
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            if(authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Xác thực không thành công");
            }

            //luu thong tin ngoi dung vao SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // timeout cho phiên làm việc
            session.setMaxInactiveInterval(60);// 1 phut
            // thiet lap session cho nguoi dung
            session.setAttribute("email", loginRequest.getEmail());
            // thiet lap cookie cho nguoi dung
            if("MANAGER".equals(authentication.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority())) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("location", "/admin/manager-dashboard").build();
            } else if("STAFF".equals(authentication.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority())) {
                return ResponseEntity.ok().body("Đăng nhập thành công với quyền STAFF");
            } else if("CUSTOMER".equals(authentication.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority())) {
                return ResponseEntity.ok().body("Đăng nhập thành công với quyền CUSTOMER");
            }
            return ResponseEntity.ok().body("Đăng nhập thành công");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email hoặc mật khẩu không chính xác");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi đăng nhập");
        }
    }


    @GetMapping("/logout")
    public String logout() {
        // Xóa thông tin xác thực khỏi SecurityContext
        SecurityContextHolder.clearContext();
        // Xóa session hiện tại
        session.invalidate();
        // Chuyển hướng về trang đăng nhập
        return "redirect:/api/login"; // Chuyển hướng đến trang đăng nhập
    }
    @GetMapping("/check-session")
    @ResponseBody
    public String checkSession() {
        Object email = session.getAttribute("email");
        if (email != null) {
            return "Đang đăng nhập với email: " + email;
        } else {
            return "Chưa đăng nhập hoặc session đã hết hạn";
        }
    }

}
