package com.example.swp.service;

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

    Optional<Order> getOrderById(int id);

    Order createOrder(OrderRequest orderRequest);

    Order save(Order order);
}
