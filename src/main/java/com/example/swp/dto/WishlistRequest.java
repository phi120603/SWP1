package com.example.swp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {
    private Long storageId; // hoặc int storageId, tùy entity của bạn
    // Mở rộng thêm trường nếu muốn (ghi chú, priority, ...)
}
