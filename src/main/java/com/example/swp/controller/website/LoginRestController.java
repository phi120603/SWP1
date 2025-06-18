package com.example.swp.controller.website;
import com.example.swp.dto.LoginRequest;
import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
import com.example.swp.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api")
public class LoginRestController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoginService loginService;
    @GetMapping("/login")
    public String returnlogin () {
        return "login"; // Spring tìm file view tên là "login"
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin customer từ DB
            Optional<Customer> customerOpt = loginService.findByEmail(loginRequest.getEmail());
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Email không tồn tại trong hệ thống");
            }
            Customer customer = customerOpt.get();
            session.setAttribute("loggedInCustomer", customer);



            return ResponseEntity.ok().body("Đăng nhập thành công");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email hoặc mật khẩu không chính xác");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi đăng nhập");
        }
    }
}
