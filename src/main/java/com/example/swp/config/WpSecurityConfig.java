package com.example.swp.config; // Đổi package cho phù hợp với dự án của bạn

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Đánh dấu đây là một lớp cấu hình Spring
public class SecurityConfig {

    @Bean // Định nghĩa một bean PasswordEncoder
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Các cấu hình bảo mật khác của bạn sẽ ở đây (ví dụ: HttpSecurity)
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     // ... cấu hình của bạn ...
    //     return http.build();
    // }
}