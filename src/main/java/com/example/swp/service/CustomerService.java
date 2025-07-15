package com.example.swp.service;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer getCustomerById(Integer id);
    List<Customer> getAll();
    Customer getCustomer(int id);
    Optional<Customer> findByEmail(String email);
    List<Customer> searchByName(String name);
    List<Customer> filterByRole(RoleName roleName);
    Customer save(Customer customer);
    void delete(int id);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
