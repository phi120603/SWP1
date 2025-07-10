package com.example.swp.dto;

import lombok.*;

import java.util.UUID;


import java.util.UUID;
import lombok.Data; // Import các annotation của Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Tự động tạo getter, setter, toString, equals, hashCode
@NoArgsConstructor // Tạo constructor không tham số
@AllArgsConstructor // Tạo constructor với tất cả các tham số
public class ChatMessageRequest {
    private String sessionId;
    private String content;

    public UUID sessionUUID() {
        return UUID.fromString(sessionId);
    }
}



