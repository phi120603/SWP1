package com.example.swp.service.impl;

import com.example.swp.dto.ChatMessageRequest;
import com.example.swp.entity.ChatMessage;
import com.example.swp.entity.ChatSession;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.mapper.ChatMapper;
import com.example.swp.repository.ChatMessageRepository;
import com.example.swp.repository.ChatSessionRepository;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.ManageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ChatService {
    private final ChatSessionRepository sesRepo;
    private final ChatMessageRepository msgRepo;

    public ChatSession newSession() {
        return sesRepo.save(new ChatSession());
    }

    @Transactional
    public ChatMessage save(UUID sid, String content, boolean mine) {
        ChatSession s = sesRepo.findById(sid).orElseThrow();
        ChatMessage m = new ChatMessage();
        m.setSession(s);
        m.setContent(content);
        m.setMine(mine);
        return msgRepo.save(m);
    }

    public List<ChatMessage> history(UUID sid) {
        return msgRepo.findBySessionIdOrderByCreatedAtAsc(sid);
    }
}
