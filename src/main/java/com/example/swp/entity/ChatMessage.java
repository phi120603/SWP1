package com.example.swp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
@Getter @Setter
public class ChatMessage {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="session_id")
    private ChatSession session;
    private String content;
    private boolean mine;
    private Instant createdAt = Instant.now();
}
