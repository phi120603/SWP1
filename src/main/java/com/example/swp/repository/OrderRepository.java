package com.example.swp.repository;

import com.example.swp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.swp.entity.Customer;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(String status);

    List<Order> findByCustomer(Customer customer);

}


