package com.example.swp.service;

import com.example.swp.entity.Customer;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CustomerService {
    List<Customer> getAll();
    public Customer getCustomer(int id);

}
