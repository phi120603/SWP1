package com.example.swp.service.impl;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Customer> searchByName(String name) {
        return customerRepository.findByFullnameContainingIgnoreCase(name);
    }

    @Override
    public List<Customer> filterByRole(RoleName roleName) {
        return customerRepository.findByRoleName(roleName);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(int id) {
        customerRepository.deleteById(id);
    }

    // THÊM:
    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }
}
