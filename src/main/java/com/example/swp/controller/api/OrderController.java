package com.example.swp.controller.api;

import com.example.swp.dto.OrderRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.example.swp.entity.Order;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/api/order")

public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping("/findByID/{id}")
    public Optional<Order> findById(@PathVariable int id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {

        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(order);
    }
}
