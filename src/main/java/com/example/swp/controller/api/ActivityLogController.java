package com.example.swp.controller.api;

import com.example.swp.entity.ActivityLog;
import com.example.swp.entity.Customer;
import com.example.swp.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    // API lấy nhật ký hoạt động của user hiện tại (từ session)
    @GetMapping("/me")
    public List<ActivityLog> getRecentActivitiesOfCurrentCustomer(
            HttpSession session,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Lấy đúng từ session key thực tế mà dự án bạn dùng ("loggedInCustomer")
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }
        List<ActivityLog> logs = activityLogRepository.findByCustomerOrderByTimestampDesc(customer);
        return logs.size() > limit ? logs.subList(0, limit) : logs;
    }
}
