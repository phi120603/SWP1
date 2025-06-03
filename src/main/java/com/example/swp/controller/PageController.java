package com.example.swp.controller;

import com.example.swp.dto.BookingRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/booking")
    public String showForm(Model model) {
        model.addAttribute("bookingRequest", new BookingRequest());
        model.addAttribute("message", "");
        return "booking"; // khớp với booking.html trong templates
    }
}
