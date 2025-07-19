package com.example.swp.controller.website;

import com.example.swp.entity.ActivityLog;
import com.example.swp.entity.Customer;
import com.example.swp.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecentActivityController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @GetMapping("/recent-activity")
    public String showRecentActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/api/login";
        }
        List<ActivityLog> logs = activityLogRepository.findByCustomerOrderByTimestampDesc(customer);
        model.addAttribute("activityLogs", logs);
        return "recent-activity";
    }
}