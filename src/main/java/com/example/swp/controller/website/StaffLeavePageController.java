package com.example.swp.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffLeavePageController {

    @GetMapping("/staff/leave-request")
    public String staffLeavePage() {
        // trả về staff-leave-request.html (bỏ đuôi .html)
        return "staff-leave-request";
    }
}
