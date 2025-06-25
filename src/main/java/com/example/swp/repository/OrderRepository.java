package com.example.swp.repository;

import com.example.swp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.swp.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(String status);

    List<Order> findByCustomer(Customer customer);

    @Modifying
    @Query("UPDATE Order o SET o.status = 'PAID' WHERE o.id = :orderId")
    void updateOrderStatusToPaid(@Param("orderId") int orderId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'PAID'")
    Double calculateTotalRevenue();

    List<Order> findTop5ByOrderByOrderDateDesc();
}


