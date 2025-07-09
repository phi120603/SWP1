package com.example.swp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_session")
    public class ChatSession {
        @Id @GeneratedValue private UUID id;
        private Instant createdAt = Instant.now();
    }

