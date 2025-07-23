package com.example.swp.aspect;

import com.example.swp.annotation.LogActivity;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.entity.RecentActivity;
import com.example.swp.entity.Staff;
import com.example.swp.service.RecentActivityService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class ActivityLoggerAspect {

    @Autowired
    private RecentActivityService recentActivityService;

    @Autowired
    private HttpSession session;

    @AfterReturning("@annotation(logActivity)")
    public void logActivity(JoinPoint joinPoint, LogActivity logActivity) {
        String actorName = "Unknown";
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        Staff staff = (Staff) session.getAttribute("loggedInStaff");
        Manager manager = (Manager) session.getAttribute("loggedInManager");

        RecentActivity activity = RecentActivity.builder()
                .action(logActivity.action())
                .entityName(logActivity.entityName())
                .entityType(logActivity.entityType())
                .detailLink(logActivity.detailLink())
                .timestamp(LocalDateTime.now())
                .actor("")
                .build();

        if (customer != null) {
            activity.setCustomer(customer);
            activity.setActor(customer.getFullname());
        } else if (staff != null) {
            activity.setStaff(staff);
            activity.setActor(staff.getFullname());
        } else if (manager != null) {
            activity.setManager(manager);
            activity.setActor(manager.getFullname());
        }else {
            activity.setActor(actorName);
        }

        recentActivityService.logActivity(activity);
    }
}
