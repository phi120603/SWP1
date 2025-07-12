package com.example.swp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChatMessageResponse {
    private UUID id;
    private String senderId;
    private String roomId;
    private String content;
    private boolean mine;
    private LocalDateTime createdAt;

}

