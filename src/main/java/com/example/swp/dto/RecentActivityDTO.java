package com.example.swp.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityDTO {
    private Integer id;
    private String action;
    private String entityName;
    private String entityType;
    private String detailLink;
    private LocalDateTime timestamp;
    private String actor;
}
