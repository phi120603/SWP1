package com.example.swp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Component
public class StorageRequest {
    private int storageid;
    private String storagename;
    private String address;
    private String city;
    private String state;
    private double area;
    private double pricePerDay;
    private String description;
    private String imUrl; // URL của ảnh đại diện kho chứa
    private boolean status;
}
