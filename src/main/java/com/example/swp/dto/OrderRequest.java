package com.example.swp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRequest {
    // Bỏ orderId vì đây là auto-generated

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate orderDate;

    // Bỏ totalAmount vì sẽ được tính trong service

    private String status; // PENDING, APPROVED, REJECTED, PAID
    private int customerId;
    private int storageId;

    // Không cần @Column annotation trong DTO
}