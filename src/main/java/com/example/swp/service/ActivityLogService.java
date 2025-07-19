package com.example.swp.service;

import com.example.swp.entity.*;
import java.util.List;

public interface ActivityLogService {
    void logActivity(String action, String description, Customer customer,
                     Order order, StorageTransaction transaction,
                     Payment payment, Feedback feedback, Issue issue);

    List<ActivityLog> getRecentActivities(Customer customer, int limit);
}
