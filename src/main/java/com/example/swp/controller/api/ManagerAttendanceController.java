package com.example.swp.controller.api;

import com.example.swp.entity.Attendance;
import com.example.swp.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/manager/attendance")
public class ManagerAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 1. Lấy toàn bộ chấm công
    @GetMapping("/all")
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    // 2. Lấy chấm công theo staffId
    @GetMapping("/by-staff/{staffId}")
    public List<Attendance> getAttendanceByStaff(@PathVariable Long staffId) {
        return attendanceService.getAttendanceByStaffId(staffId);
    }

    // 3. Lọc chấm công theo khoảng ngày
    @GetMapping("/by-date")
    public List<Attendance> getAttendanceByDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return attendanceService.getAttendanceByDateRange(from, to);
    }

    // 4. Lọc vừa theo staff vừa theo ngày (nâng cao)
    @GetMapping("/by-staff-date")
    public List<Attendance> getAttendanceByStaffIdAndDateRange(
            @RequestParam("staffId") Long staffId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return attendanceService.getAttendanceByStaffIdAndDateRange(staffId, from, to);
    }
}
