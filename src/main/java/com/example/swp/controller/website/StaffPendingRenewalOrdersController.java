package com.example.swp.controller.website;

import com.example.swp.entity.Order;
import com.example.swp.service.PendingRenewalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StaffPendingRenewalOrdersController {
    @Autowired
    private PendingRenewalOrderService pendingRenewalOrderService;

    @PostMapping("/staff/pending-renewal-orders/send-warning")
    public String sendWarningEmail(@RequestParam("orderId") Integer orderId, @RequestParam("days") int days) {
        pendingRenewalOrderService.sendRenewalWarningEmail(orderId);
        return "redirect:/staff/pending-renewal-orders?days=" + days + "&emailSent=true";
    }

    @GetMapping("/staff/pending-renewal-orders")
    public String showPendingRenewalOrders(@RequestParam(value = "days", defaultValue = "7") int days, Model model) {
        List<Order> orders = pendingRenewalOrderService.getPaidRenewalOrders(days);
        LocalDate today = LocalDate.now();

        Map<Integer, Integer> remainingDaysMap = new HashMap<>();
        for (Order order : orders) {
            int diff = today.until(order.getEndDate()).getDays();
            remainingDaysMap.put(order.getId(), diff);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("remainingDays", remainingDaysMap);
        model.addAttribute("selectedDays", days);
        return "staff-pending-renewal-orders";
    }

}