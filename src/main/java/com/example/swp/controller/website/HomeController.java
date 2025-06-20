package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
import com.example.swp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/home-page")
    public String returnhome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        model.addAttribute("username", email);

        Customer customer = customerService.findByEmail(email).orElse(null);
        long unreadCount = 0;
        if (customer != null) {
            unreadCount = notificationService.countUnreadNotifications(customer);
        }
        model.addAttribute("unreadCount", unreadCount);

        return "home-page";
    }

    @GetMapping("/")
    public String returnmain() {
        return "redirect:home-page";
    }
}
