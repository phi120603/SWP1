package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import com.example.swp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Phải cấu hình ở SecurityConfig

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("customer") Customer customer,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (customerService.existsByEmail(customer.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại!");
            return "register";
        }
        if (customerService.existsByPhone(customer.getPhone())) {
            model.addAttribute("error", "Số điện thoại đã tồn tại!");
            return "register";
        }
        customer.setRoleName(RoleName.CUSTOMER);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerService.save(customer);
        // Đăng ký thành công, chuyển về trang login
        return "redirect:/login";
    }
}
