package com.example.swp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CheckBCrypt {
    public static void main(String[] args) {
        String rawPassword = "123456"; // VD: 123456
        String hashFromDb = "$2a$10$Vv7qvBDbZKAXhdzvZaPIRur6M53WQiINgPePMB1F9mGvYjKfxtwVe"; // Dán hash lấy từ DB vào đây
        System.out.println(
                new BCryptPasswordEncoder().matches(rawPassword, hashFromDb)
        );
    }
}
