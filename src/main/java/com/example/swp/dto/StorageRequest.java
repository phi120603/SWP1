package com.example.swp.dto;

import lombok.*;
import org.springframework.stereotype.Component;
@Setter
@Getter
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
    private boolean status;
}
