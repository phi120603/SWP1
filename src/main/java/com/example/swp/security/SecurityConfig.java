package com.example.swp.security; // Đảm bảo đúng package của bạn

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Sử dụng BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Đánh dấu lớp này là một lớp cấu hình Spring
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security
public class SecurityConfig {

    // 1. Định nghĩa Bean cho PasswordEncoder
    // Spring Security yêu cầu một PasswordEncoder để mã hóa và so sánh mật khẩu

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt là một thuật toán mã hóa mật khẩu mạnh
    }

    // 2. Định nghĩa Bean cho AuthenticationManager
    // Đây là bean mà LoginController của bạn cần để thực hiện xác thực

    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 3. Cấu hình SecurityFilterChain
    // Định nghĩa các quy tắc bảo mật HTTP: trang nào được truy cập, trang đăng nhập, đăng xuất...

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF (Cross-Site Request Forgery) - Thường tắt cho API hoặc nếu bạn tự xử lý token
                // Nếu bạn có form HTML thông thường và muốn bảo vệ khỏi CSRF, hãy cân nhắc bật nó
                .csrf(csrf -> csrf.disable())

                // Cấu hình các yêu cầu được ủy quyền (authorization)
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép truy cập không cần xác thực tới các URL này
                        .requestMatchers(
                                "/login", "/SWP/login", // Trang đăng nhập của bạn
                                "/css/**", "/js/**", "/images/**", // Các tài nguyên tĩnh
                                "/register", "/forgot_password" // Các trang công khai khác
                        ).permitAll()
                        // Yêu cầu xác thực cho tất cả các request khác
                        .anyRequest().authenticated()
                )

                // Cấu hình form đăng nhập
                .formLogin(form -> form
                        .loginPage("/login") // Chỉ định trang đăng nhập của bạn
                        .loginProcessingUrl("/login") // URL mà form đăng nhập sẽ POST đến
                        .defaultSuccessUrl("/", true) // URL chuyển hướng sau khi đăng nhập thành công (true để luôn chuyển hướng)
                        .failureUrl("/SWP/login?error=true") // URL chuyển hướng khi đăng nhập thất bại
                        .permitAll() // Cho phép tất cả mọi người truy cập trang đăng nhập và quá trình xử lý đăng nhập
                )

                // Cấu hình đăng xuất
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL xử lý đăng xuất
                        .logoutSuccessUrl("/login?logout") // URL chuyển hướng sau khi đăng xuất thành công
                        .permitAll() // Cho phép tất cả mọi người truy cập quá trình đăng xuất
                );

        return http.build(); // Xây dựng và trả về SecurityFilterChain
    }
}