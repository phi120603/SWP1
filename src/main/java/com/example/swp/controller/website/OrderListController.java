package com.example.swp.controller.website;

import org.springframework.ui.Model;
import com.example.swp.entity.Order;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller

@RequestMapping("/SWP")

public class OrderListController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String listOrders(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            Model model
    ) {
        List<Order> orders;
        if ("ALL".equalsIgnoreCase(status)) {
            orders = orderService.getAllOrders();
        } else {
            orders = orderService.findOrdersByStatus(status); // Thêm hàm này
        }
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status); // Để giữ trạng thái filter
        return "order-list";
    }

}