package com.example.swp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StorageRequest {
    private int storageid;
    private String storagename;
    private String address;
    private String city;
    private String state;
    private boolean status;
}
