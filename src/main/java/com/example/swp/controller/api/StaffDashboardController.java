package com.example.swp.controller.api;

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
@RequestMapping("/staff") // đổi lại mapping cho ngắn gọn
public class StaffDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    // Dashboard staff, truy cập: http://localhost:8080/staff/dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Load all orders to display in the table
        model.addAttribute("orders", orderRepository.findAll());
        return "staffdashboard"; // File view: staffdashboard.html
    }

    // Tìm kiếm đơn theo ID, truy cập: http://localhost:8080/staff/dashboard/find?orderId=...
    @GetMapping("/dashboard/find")
    public String findOrderById(@RequestParam("orderId") Integer orderId, Model model) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            // Redirect to order detail page if found
            return "redirect:/staff/orders/" + orderId;
        } else {
            // Nếu không tìm thấy, báo lỗi và quay lại dashboard
            model.addAttribute("error", "ID không hợp lệ");
            model.addAttribute("orders", orderRepository.findAll());
            return "staffdashboard";
        }
    }
}
