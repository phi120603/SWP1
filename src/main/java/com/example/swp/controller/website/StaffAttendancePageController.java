package com.example.swp.controller.website;

import com.example.swp.entity.Staff;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class StaffAttendancePageController {
    @GetMapping("/staff-attendance")
    public String staffAttendancePage() {
        return "staff-attendance";
    }
}
