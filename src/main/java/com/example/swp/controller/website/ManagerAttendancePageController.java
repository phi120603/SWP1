package com.example.swp.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerAttendancePageController {

    @GetMapping("/manager/attendance")
    public String managerAttendancePage() {
        // Trả về tên file HTML trong /templates (bỏ .html)
        return "manager-attendance";
    }
}
