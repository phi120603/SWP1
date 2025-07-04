package com.example.swp.controller.api;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;
import com.example.swp.repository.StaffRepository;
import com.example.swp.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StaffRepository staffReponsitory;

//    private Staff getCurrentStaff(Principal principal) {
//        String email = principal.getName();
//        return staffReponsitory.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff với email: " + email));
//    }

    private Staff getCurrentStaff(Principal principal) {
        // Bỏ dùng principal, lấy staff test theo email cứng
        return staffReponsitory.findByEmail("hongquanvjp@gmail.com")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff test"));
    }

    @PostMapping("/checkin")
    public Attendance checkIn(Principal principal) {
        Staff staff = getCurrentStaff(principal);
        return attendanceService.checkIn(staff);
    }

    @PostMapping("/checkout")
    public Attendance checkOut(Principal principal) {
        Staff staff = getCurrentStaff(principal);
        return attendanceService.checkOut(staff);
    }

    @GetMapping("/history")
    public List<Attendance> getAttendanceHistory(Principal principal) {
        Staff staff = getCurrentStaff(principal);
        return attendanceService.getAttendanceByStaff(staff);
    }
}
