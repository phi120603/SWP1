package com.example.swp.dto;

import com.example.swp.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotNull(message = "Conversation ID is required")
    private UUID conversationId;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private MessageType messageType = MessageType.TEXT;

    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
}
