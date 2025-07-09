package com.example.swp.dto;

import lombok.*;

import java.util.UUID;

//@AllArgsConstructor
//@NoArgsConstructor
//@Data

public record ChatMessageResponse(
        UUID messageId,
        UUID sessionId,
        String content,
        String sender,
        long timestamp,
        boolean seen
) {}

