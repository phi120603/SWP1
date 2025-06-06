package com.example.swp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class MyUserDetailService {
    @Bean
// authentication
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("password"))
                .roles("ADMIN")
                .build();
        UserDetails staff = User.withUsername("staff")
                .password(encoder.encode("pws"))
                .roles("STAFF")
                .build();
        UserDetails user = User.withUsername("customer")
                .password(encoder.encode("pwd1"))
                .roles("CUSTOMER")
                .build();
        return new InMemoryUserDetailsManager(admin,staff, user);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

}
