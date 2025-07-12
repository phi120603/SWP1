package com.example.swp.controller.api;

import com.example.swp.dto.RegisterRequest;
import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import com.example.swp.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisterApiController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (customerService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }
        if (customerService.existsByPhone(request.getPhone())) {
            return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại!");
        }

        Customer customer = new Customer();
        customer.setFullname(request.getFullname());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setId_citizen(request.getId_citizen());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setRoleName(RoleName.CUSTOMER);

        customerService.save(customer);
        return ResponseEntity.ok("Đăng ký thành công!");
    }
}
