package com.example.swp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recent_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String actor;

    private String entityName;

    private String entityType;

    private String detailLink;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}