package com.example.swp.controller.api;

import com.example.swp.dto.ChatMessageRequest;
import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.service.impl.ChatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    // API: Lấy lịch sử tin nhắn của 1 phòng
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @RequestParam String userId1,
            @RequestParam String userId2
    ) {
        String roomId = chatService.generateRoomIdBetween(userId1, userId2);
        List<ChatMessageResponse> messages = chatService.getMessagesByRoom(roomId, userId1);
        return ResponseEntity.ok(messages);
    }


    // API: Gửi tin nhắn (qua REST, dùng để lưu)
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ChatMessageResponse response = chatService.save(request);
        return ResponseEntity.ok(response);
    }

    // API: Lấy thông tin session (roomId & senderId)
    @GetMapping("/session/dev")
    public ResponseEntity<?> getSession(HttpSession session) {
        // ✅ Nếu userId chưa có thì tạo tạm
        String senderId = (String) session.getAttribute("userId");
        if (senderId == null) {
            senderId = "user-" + System.currentTimeMillis(); // Tạo user giả lập
            session.setAttribute("userId", senderId);
        }

        // ✅ Tạm thời dùng userId làm roomId (1-1 chat)
        String roomId = "room-" + senderId;

        Map<String, String> result = new HashMap<>();
        result.put("session", roomId);
        result.put("senderId", senderId);
        return ResponseEntity.ok(result);
    }
}
