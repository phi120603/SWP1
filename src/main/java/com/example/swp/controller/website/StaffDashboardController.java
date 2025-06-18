package com.example.swp.controller.website; // Đặt vào đúng package website cho phân quyền dễ quản lý

import com.example.swp.entity.Order;
import com.example.swp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Load all orders to display in the table
        model.addAttribute("orders", orderRepository.findAll());
        return "staffdashboard"; // /templates/staffdashboard.html
    }

    @GetMapping("/dashboard/find")
    public String findOrderById(@RequestParam("orderId") Integer orderId, Model model) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            // Redirect to order detail page if found
            return "redirect:/staff/orders/" + orderId;
        } else {
            // If not found, show error and reload dashboard
            model.addAttribute("error", "ID không hợp lệ");
            model.addAttribute("orders", orderRepository.findAll());
            return "staffdashboard";
        }
    }
}
