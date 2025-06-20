package com.example.swp.service;

import com.example.swp.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAll();
    Customer getCustomer(int id);
    Optional<Customer> findByEmail(String email); // Chuẩn!
}
