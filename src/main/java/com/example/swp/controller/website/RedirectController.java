package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/redirect-by-role")
    public String handleRedirectByRole(Authentication authentication) {
        Customer user = (Customer) authentication.getPrincipal();
        String role = user.getRoleName().name();

        return switch (role) {
            case "MANAGER" -> "redirect:/manager/dashboard";
            case "STAFF" -> "redirect:/staff/dashboard";
            case "CUSTOMER" -> "redirect:/customer/home";
            default -> "redirect:/";
        };
    }
}
