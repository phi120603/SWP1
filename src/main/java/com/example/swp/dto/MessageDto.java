package com.example.swp.dto;

import com.example.swp.enums.MessageType;
import com.example.swp.enums.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private UUID id;
    private UUID conversationId;
    private Integer senderId;
    private String senderName;
    private SenderType senderType;
    private String content;
    private MessageType messageType;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private boolean isMine;
}
