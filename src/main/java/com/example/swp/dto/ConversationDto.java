package com.example.swp.dto;

import com.example.swp.enums.ConversationStatus;
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
public class ConversationDto {
    private UUID id;
    private Integer customerId;
    private String customerName;
    private String customerEmail;
    private Integer managerId;
    private String managerName;
    private String managerEmail;
    private ConversationStatus status;
    private boolean customerUnread;
    private boolean managerUnread;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long unreadCount;
    private boolean isOnline;
}
