package com.example.swp.service;

import com.example.swp.entity.Customer;
import com.example.swp.dto.OrderRequest;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface OrderService {
    List<Order> getAllOrders();

    List<Order> findOrdersByStatus(String status);

    Optional<Order> getOrderById(int id);

    Order createOrder(OrderRequest orderRequest);

    List<Order> findOrdersByCustomer(Customer customer);

    Order save(Order order);

    double getTotalRevenueAll(); // Tổng tiền các đơn trừ REJECTED
    double getRevenuePaid();     // Tiền khách đã trả (PAID)
    double getRevenueApproved(); // Tiền khách còn nợ (APPROVED)
    void deleteById(int id);

}
