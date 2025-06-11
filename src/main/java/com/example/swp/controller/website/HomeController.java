package com.example.swp.controller.website;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/SWP")
public class HomeController {
    @GetMapping("/homepage")
    public String returnhome (Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        return"home-page";
    }
    @GetMapping("/")
    public String returnmain () {
        return"redirect:homepage";
    }

}
