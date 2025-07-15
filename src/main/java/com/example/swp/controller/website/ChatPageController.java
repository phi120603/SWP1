package com.example.swp.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {
    @GetMapping("/qvl-chatbot")
    public String showChatbotPage() {
        return "qvl-chatbot"; // KHÔNG có .html ở đây!
    }
}
