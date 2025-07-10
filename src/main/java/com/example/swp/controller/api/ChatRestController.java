package com.example.swp.controller.api;

import com.example.swp.dto.ChatMessageRequest;
import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.entity.ChatMessage;
import com.example.swp.entity.ChatSession;
import com.example.swp.repository.ChatMessageRepository;
import com.example.swp.repository.ChatSessionRepository;
import com.example.swp.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRestController {

    private final ChatService svc;
    @Autowired
    ChatSessionRepository sessionRepo;
    @Autowired
    ChatMessageRepository chatRepo;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private ChatSessionRepository sessionRepository;

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageRequest request) {
        // Validate request
        if (request.getContent() == null || request.getContent().isBlank()) {
            return ResponseEntity.badRequest().body("Message content is required.");
        }

        UUID sessionId;
        try {
            sessionId = UUID.fromString(request.getSessionId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid session ID format.");
        }

        Optional<ChatSession> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            return ResponseEntity.badRequest().body("Session not found.");
        }

        // Tạo và lưu message
        ChatMessage message = ChatMessage.builder()
                .session(optionalSession.get())
                .content(request.getContent())
                .mine(true)
                .build();

        ChatMessage saved = messageRepository.save(message);

        // Trả về DTO để tránh vòng lặp hoặc dữ liệu dư
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .mine(saved.isMine())
                .createdAt(saved.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }




    @GetMapping("/session/dev")
    public Map<String, UUID> devSession() {
        ChatSession s = sessionRepo.findTopByOrderByCreatedAtAsc()
                .orElseThrow(() -> new RuntimeException("No session in DB"));
        return Map.of("session", s.getId());
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