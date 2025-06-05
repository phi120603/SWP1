package com.example.swp.service;

import com.example.swp.dto.LoginRequest;
import com.example.swp.dto.LoginResponse;
import com.example.swp.dto.RegisterRequest;
import com.example.swp.entity.Customer; // Hoặc User

public interface CustomerService {
    Customer register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}