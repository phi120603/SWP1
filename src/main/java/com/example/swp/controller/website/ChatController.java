package com.example.swp.controller.website;

import com.example.swp.dto.ChatMessageRequest;
import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.entity.ChatMessage;
//import com.example.swp.mapper.ChatMapper;
import com.example.swp.security.MyUserDetail;
import com.example.swp.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller @RequiredArgsConstructor

public class ChatController {
    private final ChatService svc; private final SimpMessagingTemplate ws;

    @GetMapping("/chat")
    public String chatPage() {
        return "index";
    }


//    /* REST tạo session */
//    @PostMapping("/api/session")
//    public Map<String, UUID> create() {
//        return Map.of("session", svc.newSession().getId());
//    }
//
//    /* REST lấy lịch sử */
//    @GetMapping("/api/history/{sid}")
//    public List<ChatMessage> hist(@PathVariable UUID sid) {
//        return svc.history(sid);
//    }
//
//    /* WS nhận tin */
//    @MessageMapping("/send/{sid}")
//    public void msg(@DestinationVariable UUID sid, @Payload String text, Principal p) {
//        ChatMessage saved = svc.save(sid, text, true);
//        ws.convertAndSend("/topic/" + sid, saved);
//    }
}


