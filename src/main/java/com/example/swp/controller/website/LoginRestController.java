package com.example.swp.controller.website;

import com.example.swp.dto.LoginRequest;
import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
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

    @Autowired private HttpSession session;
    @Autowired private AuthenticationManager authManager;
    @Autowired private CustomerService customerService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home-page";
        }
        model.addAttribute("sessionId", session.getId());
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> doLogin(@RequestBody LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);

            session.setMaxInactiveInterval(600);
            session.setAttribute("email", req.getEmail());
            // lưu Customer vào session để dùng tiếp
            Object principal = auth.getPrincipal();
            if (principal instanceof Customer) {
                session.setAttribute("loggedInCustomer", principal);
            }
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            String redirect = "/home-page";
            for (GrantedAuthority ga : auth.getAuthorities()) {
                String role = ga.getAuthority();
                if ("MANAGER".equals(role))      redirect = "/admin/manager-dashboard";
                else if ("STAFF".equals(role))   redirect = "/staff/dashboard";
                else if ("CUSTOMER".equals(role))redirect = "/home-page";
                break;
            }

            Map<String,String> body = new HashMap<>();
            body.put("redirect", redirect);
            return ResponseEntity.ok(body);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email hoặc mật khẩu không đúng.");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống, vui lòng thử lại.");
        }
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/api/login";
    }
}
