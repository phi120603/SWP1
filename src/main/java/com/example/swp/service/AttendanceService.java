package com.example.swp.service;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceService {
    Attendance checkIn(Staff staff);
    Attendance checkOut(Staff staff);
    List<Attendance> getAttendanceByStaff(Staff staff);

    List<Attendance> getAllAttendance(); // Lấy tất cả chấm công

    List<Attendance> getAttendanceByStaffId(Long staffId); // Lấy theo staffId

    List<Attendance> getAttendanceByDateRange(LocalDateTime from, LocalDateTime to); // Lọc theo ngày

    List<Attendance> getAttendanceByStaffIdAndDateRange(Long staffId, LocalDateTime from, LocalDateTime to); // Lọc nâng cao (theo staff và ngày)

}
