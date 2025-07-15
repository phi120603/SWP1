package com.example.swp.config;

import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
import com.example.swp.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CustomerService customerService;

    @ModelAttribute
    public void addUnreadCount(Model model, HttpSession session) {
        // Lấy email từ SecurityContextHolder (ổn định nhất)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            email = auth.getName();
        } else {
            // Nếu không có Security, fallback sang session
            email = (String) session.getAttribute("email");
        }

        if (email != null) {
            Customer customer = customerService.findByEmail(email).orElse(null);
            if (customer != null) {
                long unreadCount = notificationService.countUnreadNotifications(customer);
                model.addAttribute("unreadCount", unreadCount);
            }

        }
        }
    }

