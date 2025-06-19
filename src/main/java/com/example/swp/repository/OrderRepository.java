package com.example.swp.repository;

import com.example.swp.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Modifying
    @Query("UPDATE Order o SET o.status = 'PAID' WHERE o.id = :orderId")
    void updateOrderStatusToPaid(@Param("orderId") int orderId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'PAID'")
    Double calculateTotalRevenue();

}
