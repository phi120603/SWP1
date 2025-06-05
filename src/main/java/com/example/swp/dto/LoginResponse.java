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
    // private String accessToken; // Nếu bạn dùng JWT

    /*
    // Nếu không dùng Lombok, bạn cần định nghĩa getters và setters thủ công:
    public LoginResponse() {}

    public LoginResponse(String username, String customerId) { // constructor
        this.username = username;
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) { // Đảm bảo tham số là String
        this.customerId = customerId;
    }

    // public String getAccessToken() {
    //     return accessToken;
    // }
    //
    // public void setAccessToken(String accessToken) {
    //     this.accessToken = accessToken;
    // }
    */
}