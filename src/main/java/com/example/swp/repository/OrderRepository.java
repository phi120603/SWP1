package com.example.swp.repository;

import com.example.swp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.swp.entity.Customer;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(String status);

    List<Order> findByCustomer(Customer customer);

    @Query("SELECT o FROM Order o WHERE o.status = 'PAID' AND o.endDate <= :threshold AND o.endDate >= :today")
    List<Order> findPaidRenewalOrders(LocalDate today, LocalDate threshold);

}


