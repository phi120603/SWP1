package com.example.swp.service.impl;

import com.example.swp.entity.*;
import com.example.swp.repository.ActivityLogRepository;
import com.example.swp.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public void logActivity(String action, String description, Customer customer,
                            Order order, StorageTransaction transaction,
                            Payment payment, Feedback feedback, Issue issue) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setDescription(description);
        log.setCustomer(customer);
        log.setOrder(order);
        log.setTransaction(transaction);
        log.setPayment(payment);
        log.setFeedback(feedback);
        log.setIssue(issue);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    @Override
    public List<ActivityLog> getRecentActivities(Customer customer, int limit) {
        List<ActivityLog> all = activityLogRepository.findByCustomerOrderByTimestampDesc(customer);
        return all.size() > limit ? all.subList(0, limit) : all;
    }
}
