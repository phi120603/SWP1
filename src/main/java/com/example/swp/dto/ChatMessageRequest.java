package com.example.swp.dto;

import lombok.*;

import java.util.UUID;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Data

public record ChatMessageRequest(String sessionId, String content) {
    public UUID sessionUUID() {
        return UUID.fromString(sessionId);
    }
}



