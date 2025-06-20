package com.example.swp.service.impl;

import com.example.swp.dto.OrderRequest;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.OrderRepository;
import com.example.swp.repository.StorageReponsitory;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class OrderServiceimpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StorageReponsitory storageReponsitory;
    @Autowired
    private StorageRequest storageRequest;
    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(int id) {return orderRepository.findById(id);}

    @Override
    public List<Order> findOrdersByCustomer(Customer customer) {
        return orderRepository.findByCustomer(customer);
    }


    @Override
    public Order createOrder(OrderRequest orderRequest) {
        Customer customer =  customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("ko co customer " +orderRequest.getCustomerId()));
        Storage storage = storageReponsitory.findById(orderRequest.getStorageId())
                .orElseThrow(() -> new RuntimeException("ko co storage " +orderRequest.getStorageId()));
        Order order = new Order();
        order.setStartDate(orderRequest.getStartDate());
        order.setEndDate(orderRequest.getEndDate());
        order.setOrderDate(orderRequest.getOrderDate());
        long rentalDays = ChronoUnit.DAYS.between(orderRequest.getStartDate(), orderRequest.getEndDate());
        // Tính tổng tiền thuê
        double dailyRate =storage.getPricePerDay(); // hoặc giá cố định
        double totalAmount = rentalDays * dailyRate;
        order.setTotalAmount(totalAmount);
        order.setStatus(orderRequest.getStatus());
        order.setCustomer(customer);
        order.setStorage(storage);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }


    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }



    @Override
    public double getTotalRevenueAll() {
        return orderRepository.findAll()
                .stream()
                .filter(order -> !"REJECTED".equalsIgnoreCase(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    @Override
    public double getRevenuePaid() {
        return orderRepository.findAll()
                .stream()
                .filter(order -> "PAID".equalsIgnoreCase(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    @Override
    public double getRevenueApproved() {
        return orderRepository.findAll()
                .stream()
                .filter(order -> "APPROVED".equalsIgnoreCase(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    //Hàm tính total amount
    public BigDecimal calculateTotalAmount(LocalDate startDate, LocalDate endDate, BigDecimal pricePerDay) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        return pricePerDay.multiply(BigDecimal.valueOf(days));


    }

    @Transactional
    public void markOrderAsPaid(int orderId) {
        orderRepository.updateOrderStatusToPaid(orderId);
    }


}
