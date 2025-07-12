package com.example.swp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.swp.service.ChatbotService;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<String> askAI(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = chatbotService.askAI(message); // Gọi method mới
        return ResponseEntity.ok(reply);
    }
}
