package com.example.swp.controller.api;

import com.example.swp.entity.Storage;
import org.springframework.ui.Model;
import com.example.swp.entity.Order;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/api/order")

public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/all-orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping("/findByID/{id}")
    public Optional<Order> findById(@PathVariable int id) {
        return orderService.getOrderById(id);
    }
}
