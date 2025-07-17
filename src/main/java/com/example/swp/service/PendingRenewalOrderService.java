package com.example.swp.service;

import com.example.swp.entity.Order;

import java.util.List;

public interface PendingRenewalOrderService {
    List<Order> getPaidRenewalOrders(int days);
    void sendRenewalWarningEmail(Integer orderId);
}