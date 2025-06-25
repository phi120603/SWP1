package com.example.swp.service;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAll();
    Customer getCustomer(int id);
    List<Customer> searchByName(String name);
    List<Customer> filterByRole(RoleName roleName);
    Customer save(Customer customer);
    void delete(int id);
    Customer findByEmail(String email);
    // THÊM:

}
