package com.example.swp.controller.website;

import org.springframework.ui.Model;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        model.addAttribute("totalRevenueAll", totalRevenueAll);
        model.addAttribute("revenuePaid", revenuePaid);
        model.addAttribute("revenueApproved", revenueApproved);

        return "revenue-statistics";
    }
}
