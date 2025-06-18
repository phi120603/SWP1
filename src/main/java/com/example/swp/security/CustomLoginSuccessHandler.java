package com.example.swp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectURL = request.getContextPath();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_CUSTOMER")) {
                redirectURL = "/customer/home";
                break;
            } else if (role.equals("ROLE_STAFF")) {
                redirectURL = "/staff/dashboard";
                break;
            } else if (role.equals("ROLE_MANAGER")) {
                redirectURL = "/manager/admin";
                break;
            }
        }
        response.sendRedirect(redirectURL);
    }
}
