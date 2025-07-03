package com.example.swp.service;

import com.example.swp.entity.Customer;
import com.example.swp.dto.OrderRequest;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public interface OrderService {
    List<Order> getAllOrders();



    List<Order> findOrdersByStatus(String status);

    Optional<Order> getOrderById(int id);

    Order createOrder(OrderRequest orderRequest);

    List<Order> findOrdersByCustomer(Customer customer);
    long countOverlapOrdersByCustomer(int customerId, int storageId, LocalDate startDate, LocalDate endDate);


    Order save(Order order);

    double getTotalRevenueAll(); // Tổng tiền các đơn trừ REJECTED
    double getRevenuePaid();     // Tiền khách đã trả (PAID)
    double getRevenueApproved(); // Tiền khách còn nợ (APPROVED)
    void deleteById(int id);
    boolean isStorageAvailable(int storageId, java.time.LocalDate startDate, java.time.LocalDate endDate);

    // (tuỳ chọn, nhưng giúp code controller gọn hơn)
    Order createBookingOrder(Storage storage, Customer customer, java.time.LocalDate startDate, java.time.LocalDate endDate, double total);


}
