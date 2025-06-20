package com.example.swp.service;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
    Customer getCustomer(int id);

    // Thêm nếu muốn search/filter
    List<Customer> searchByName(String name);
    List<Customer> filterByRole(RoleName roleName);

    Customer save(Customer customer);
    void delete(int id);

}
