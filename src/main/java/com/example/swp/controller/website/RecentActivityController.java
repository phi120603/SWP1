package com.example.swp.controller.website;

import com.example.swp.entity.ActivityLog;
import com.example.swp.entity.Customer;
import com.example.swp.entity.RecentActivity;
import com.example.swp.service.ActivityLogService;
import com.example.swp.service.RecentActivityService;
import com.example.swp.service.impl.RecentActivityServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class RecentActivityController {

    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private RecentActivityServiceImpl recentActivityServiceImpl;

    @GetMapping("/SWP/activity/recent") // ✅ sửa đường dẫn cho đồng bộ
    public String showRecentActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/login";
        }
        List<ActivityLog> logs = activityLogService.getAllByCustomer(customer);
        model.addAttribute("activityLogs", logs);
        model.addAttribute("customer", customer);

        return "customer-recent-activity-list";
    }

    @GetMapping("/SWP/activity/order")
    public String showOrderActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";
        List<ActivityLog> logs = activityLogService.getByOrder(customer);
        model.addAttribute("activityLogs", logs);
        model.addAttribute("customer", customer);
        return "activity-order"; // cần tạo file activity-order.html
    }

    @GetMapping("/SWP/activity/feedback")
    public String showFeedbackActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";
        List<ActivityLog> logs = activityLogService.getByFeedback(customer);
        model.addAttribute("activityLogs", logs);
        model.addAttribute("customer", customer);
        return "activity-feedback"; // cần tạo activity-feedback.html
    }

    @GetMapping("/SWP/activity/account")
    public String showAccountActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";
        List<ActivityLog> logs = activityLogService.getByAccount(customer);
        model.addAttribute("activityLogs", logs);
        model.addAttribute("customer", customer);
        return "activity-account";
    }

    @GetMapping("/SWP/activity/issue")
    public String showIssueActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";

        List<ActivityLog> logs = activityLogService.getByIssue(customer);
        model.addAttribute("activityLogs", logs);
        model.addAttribute("customer", customer);
        return "activity-issue"; // cần tạo file activity-issue.html
    }

    @GetMapping("/SWP/activity/transaction")
    public String showTransactionActivity(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";
        List<ActivityLog> logs = activityLogService.getByTransaction(customer);
        model.addAttribute("activityLogs", logs);
        model.addAttribute("customer", customer);
        return "activity-transaction"; // sẽ cần tạo activity-transaction.html
    }
}



