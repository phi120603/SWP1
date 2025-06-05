package com.example.swp.service.impl;

import com.example.swp.dto.LoginRequest;
import com.example.swp.dto.LoginResponse;
import com.example.swp.dto.RegisterRequest;
import com.example.swp.entity.Customer;
import com.example.swp.enums.Role;
import com.example.swp.reponsitory.CustomerReponsitory;
import com.example.swp.service.CustomerService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerReponsitory customerReponsitory;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerReponsitory customerReponsitory, PasswordEncoder passwordEncoder) {
        this.customerReponsitory = customerReponsitory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Customer register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty() ||
                request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Username, password, and email cannot be empty.");
        }

        if (customerReponsitory.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' already exists.");
        }

        if (customerReponsitory.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' already exists.");
        }

        Customer newCustomer = new Customer();
        newCustomer.setUsername(request.getUsername());
        newCustomer.setPassword(passwordEncoder.encode(request.getPassword()));
        newCustomer.setEmail(request.getEmail());
        newCustomer.setFullname(request.getFullname());
        newCustomer.setAddress(request.getAddress());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setId_citizen(request.getIdCitizen());

        newCustomer.setRole(Role.CUSTOMER);

        return customerReponsitory.save(newCustomer);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        Optional<Customer> customerOptional = customerReponsitory.findByUsername(request.getUsername());

        if (customerOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        Customer customer = customerOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        LoginResponse response = new LoginResponse();
        response.setId(customer.getId());
        response.setUsername(customer.getUsername());
        response.setEmail(customer.getEmail());
        response.setFullname(customer.getFullname());
        response.setRole(customer.getRole().name());
        response.setMessage("Login successful!");
        // private String token;

        return response;
    }
}