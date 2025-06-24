package com.example.swp.controller.website;

import org.springframework.ui.Model;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/SWP/statistics")
public class RevenueStatisticsController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showStatistics(Model model) {
        double totalRevenueAll = orderService.getTotalRevenueAll();
        double revenuePaid = orderService.getRevenuePaid();
        double revenueApproved = orderService.getRevenueApproved();

        // Đếm số lượng từng trạng thái
        Map<String, Long> statusCounts = orderService.countOrdersByStatus();
        long totalOrders = statusCounts.values().stream().mapToLong(Long::longValue).sum();

        long paidCount = statusCounts.getOrDefault("Paid", 0L);
        long approvedCount = statusCounts.getOrDefault("Approved", 0L);
        long pendingCount = statusCounts.getOrDefault("Pending", 0L);
        long rejectedCount = statusCounts.getOrDefault("Rejected", 0L);
        long otherCount = totalOrders - paidCount - approvedCount;

        // Tính phần trăm
        double percentPaid = totalOrders > 0 ? (paidCount * 100.0 / totalOrders) : 0;
        double percentApproved = totalOrders > 0 ? (approvedCount * 100.0 / totalOrders) : 0;
        double percentOther = totalOrders > 0 ? (otherCount * 100.0 / totalOrders) : 0;
        double percentRejected = totalOrders > 0 ? (rejectedCount * 100.0 / totalOrders) : 0;

        model.addAttribute("totalRevenueAll", totalRevenueAll);
        model.addAttribute("revenuePaid", revenuePaid);
        model.addAttribute("revenueApproved", revenueApproved);

        model.addAttribute("percentPaid", percentPaid);
        model.addAttribute("percentApproved", percentApproved);
        model.addAttribute("percentOther", percentOther);
        model.addAttribute("percentRejected", percentRejected);

        return "revenue-statistics";
    }
}
