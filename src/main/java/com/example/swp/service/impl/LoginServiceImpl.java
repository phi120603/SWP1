
package com.example.swp.service.impl;

import com.example.swp.dto.LoginRequest;
import com.example.swp.entity.Customer;
import com.example.swp.repository.CustomerRepository;

import com.example.swp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}

