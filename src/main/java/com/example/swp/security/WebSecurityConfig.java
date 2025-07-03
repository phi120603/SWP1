package com.example.swp.security;

import com.example.swp.enums.RoleName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // cau hinh
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authConfig -> {
                    // ====== BẬT CHẾ ĐỘ TEST KHÔNG CẦN LOGIN ======
                    authConfig.anyRequest().permitAll(); // Mở toàn bộ quyền, không cần đăng nhập

                    // ====== PHÂN QUYỀN GỐC, COMMENT LẠI TẠM THỜI ======
                    // authConfig.requestMatchers(HttpMethod.GET, "/admin/**").hasAuthority(RoleName.MANAGER.toString());
                    // authConfig.requestMatchers(HttpMethod.GET, "/staff/**").hasAuthority(RoleName.STAFF.toString());
                    // authConfig.requestMatchers(HttpMethod.GET, "/home/**").permitAll();
                    // authConfig.anyRequest().permitAll();
                    // ====== KẾT THÚC PHÂN QUYỀN GỐC ======
                }).formLogin(login -> {
                    login.loginPage("/login");
                    login.failureUrl("/login?error=true");
                    login.defaultSuccessUrl("/");
                }).logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                    logout.deleteCookies("JSESSIONID");
                    logout.deleteCookies("JWT_TOKEN");
                    logout.deleteCookies("1234abc");
                    logout.deleteCookies("4567abc");
                    logout.deleteCookies("89abc");
                    logout.invalidateHttpSession(true);
                })
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }


    // Bổ sung bean AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
