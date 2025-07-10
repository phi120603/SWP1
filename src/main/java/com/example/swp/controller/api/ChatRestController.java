package com.example.swp.controller.api;

import com.example.swp.entity.ChatMessage;
import com.example.swp.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRestController {

    private final ChatService svc;

    @PostMapping("/message")
    public ChatMessage send(@RequestBody Map<String, Object> body) {
        UUID sessionId = UUID.fromString(body.get("sessionId").toString());
        String content = body.get("content").toString();
        return svc.send(sessionId, content, true); // true = mine
    }



    @PostMapping("/session")
    public Map<String, UUID> create() {
        return Map.of("session", svc.newSession().getId());
    }

    @GetMapping("/history/{sid}")
    public List<ChatMessage> hist(@PathVariable UUID sid) {
        return svc.history(sid);
    }
}