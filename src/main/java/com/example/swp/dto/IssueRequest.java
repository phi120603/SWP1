package com.example.swp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueRequest {
    private String subject;
    private String description;
    private Integer customerId;       // <-- Đổi từ int sang Integer
    private Integer assignedStaffId;  // <-- Đổi từ int sang Integer
}

