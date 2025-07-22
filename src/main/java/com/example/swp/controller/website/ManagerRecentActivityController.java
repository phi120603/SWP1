package com.example.swp.controller.website;

import com.example.swp.entity.RecentActivity;
import com.example.swp.service.RecentActivityService;
import com.example.swp.service.impl.RecentActivityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("/recent-activity")
public class ManagerRecentActivityController {
    @Autowired
    private RecentActivityService recentActivityService;

    @GetMapping("/login")
    public String getLoginActivities(Model model) {
        List<RecentActivity> loginActivities = recentActivityService.getLoginActivities();
        model.addAttribute("activities", loginActivities);
        return "recent-activity-login";
    }

    @GetMapping("/voucher")
    public String getVoucherActivities(Model model) {
        List<RecentActivity> voucherActivities = recentActivityService.getVoucherActivities();
        model.addAttribute("activities", voucherActivities);
        return "recent-activity-voucher";
    }

    @GetMapping("/storage")
    public String getStorageActivities(Model model) {
        List<RecentActivity> storageActivities = recentActivityService.getStorageActivities();
        model.addAttribute("activities", storageActivities);
        return "recent-activity-storages";
    }

    @GetMapping("/order")
    public String getOrderActivities(Model model) {
        List<RecentActivity> orderActivities = recentActivityService.getOrderActivities();
        model.addAttribute("activities", orderActivities);
        return "recent-activity-order";
    }
}

