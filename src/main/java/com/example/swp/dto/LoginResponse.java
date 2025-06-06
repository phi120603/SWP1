package com.example.swp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;           // Nếu bạn dùng Lombok
import lombok.NoArgsConstructor; // Nếu bạn dùng Lombok
import lombok.Setter;           // Nếu bạn dùng Lombok

@Getter
@Setter
@NoArgsConstructor // Nếu bạn dùng Lombok, sẽ tự tạo constructor không đối số
@AllArgsConstructor // Nếu bạn dùng Lombok, sẽ tự tạo constructor với tất cả đối số
public class LoginResponse {
    private String username;
    private String customerId; // Thay đổi kiểu dữ liệu từ 'int' thành 'String'
}