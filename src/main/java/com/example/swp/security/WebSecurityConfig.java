package com.example.swp.security;

import com.example.swp.enums.RoleName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // cau hinh
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authConfig -> {
            authConfig.requestMatchers(HttpMethod.GET, "/admin/**").hasAuthority(RoleName.MANAGER.toString());
            authConfig.requestMatchers(HttpMethod.GET, "/staff/**").hasAuthority(RoleName.STAFF.toString());
            authConfig.requestMatchers(HttpMethod.GET, "/home/**").permitAll();
            authConfig.anyRequest().permitAll();
        }).formLogin(login -> {
            login.loginPage("/login");
            login.failureUrl("/login?error=true");
            login.defaultSuccessUrl("/");
        }).logout(logout -> {
//                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
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
}
