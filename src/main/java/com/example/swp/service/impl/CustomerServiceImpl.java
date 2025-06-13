package com.example.swp.service.impl;

import com.example.swp.entity.Customer;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(int id) {
        return null;
    }

//    @Override
//    Optional<Customer> getCustomer(int id) {
//        return customerRepository.findById(id);
//    }
}
