//package com.example.swp.config; // Đảm bảo package chính xác
//
//import com.example.swp.security.WebSecurityConfig;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration // Đánh dấu đây là lớp cấu hình của Spring
//@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security
//@AllArgsConstructor
//public class WpSecurityConfig {
//
//    // Định nghĩa một bean PasswordEncoder để mã hóa mật khẩu
//    // Đây là bean mà CustomerServiceImpl cần để autowire PasswordEncoder
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // Cấu hình chuỗi lọc bảo mật cho các yêu cầu HTTP
////    @Bean
//
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http.authorizeHttpRequests((requests) ->
////                requests
////                        .requestMatchers("/resources/templates/*").permitAll()
////                        .anyRequest().authenticated()
////        );
////        http
////                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho API REST (trong môi trường sản phẩm cần cân nhắc bật lại)
////                .authorizeHttpRequests(authorize -> authorize
////                        // Cho phép các request đến API đăng ký và đăng nhập mà không cần xác thực
////                        .requestMatchers("/api/*").permitAll()
////                        // Cho phép truy cập các tài nguyên tĩnh và trang lỗi (nếu có)
//////                        .requestMatchers("/css/**", "/js/**", "/images/**", "/error").permitAll()
////                        // Tất cả các request khác yêu cầu người dùng đã được xác thực
////                        .anyRequest().authenticated()
////                )
////                // Vì bạn đang xây dựng REST API, bạn có thể không cần cấu hình formLogin truyền thống
////                // Nếu bạn cần form login HTML, bạn sẽ thêm nó vào đây
////                 .formLogin(form -> form.disable()
//////                     .loginPage("/login").permitAll() // Đường dẫn đến trang login HTML của bạn
//////                     .defaultSuccessUrl("/", true) // Chuyển hướng sau khi login thành công
//////                     .permitAll()
////                 )
////                .logout(logout -> logout
////                        .logoutUrl("/")// URL cho API logout
////                        .logoutSuccessUrl("/") // Chuyển hướng sau khi logout
////                        .permitAll()
////                );
////        return http.build();
////    }
//}