package com.example.swp.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerLeavePageController {

    @GetMapping("/manager/leave-requests")
    public String managerLeavePage() {
        // trả về manager-leave-requests.html (bỏ đuôi .html)
        return "manager-leave-requests";
    }
}
