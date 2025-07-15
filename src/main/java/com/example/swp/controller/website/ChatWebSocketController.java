package com.example.swp.controller.website;

import com.example.swp.dto.ChatMessageRequest;
import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // Nhận tin nhắn từ WebSocket client (gửi đến /app/chat)
    @MessageMapping("/chat")
    public void handleChatMessage(ChatMessageRequest request) {
        // 1. Lưu vào DB
        ChatMessageResponse savedMessage = chatService.save(request);

        // 2. Broadcast đến tất cả client đang ở room tương ứng
        messagingTemplate.convertAndSend(
                "/topic/room/" + savedMessage.getRoomId(),
                savedMessage
        );
    }
}
