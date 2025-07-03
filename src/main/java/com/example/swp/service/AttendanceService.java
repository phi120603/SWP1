package com.example.swp.service;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;

import java.util.List;

public interface AttendanceService {
    Attendance checkIn(Staff staff);
    Attendance checkOut(Staff staff);
    List<Attendance> getAttendanceByStaff(Staff staff);
}
