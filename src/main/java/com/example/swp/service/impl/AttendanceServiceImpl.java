package com.example.swp.service.impl;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;
import com.example.swp.repository.AttendanceRepository;
import com.example.swp.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Attendance checkIn(Staff staff) {
        Attendance attendance = new Attendance();
        attendance.setStaff(staff);
        attendance.setCheckInTime(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance checkOut(Staff staff) {
        List<Attendance> list = attendanceRepository.findByStaff(staff);
        for (Attendance att : list) {
            if (att.getCheckOutTime() == null) {
                att.setCheckOutTime(LocalDateTime.now());
                return attendanceRepository.save(att);
            }
        }
        return null;
    }

    @Override
    public List<Attendance> getAttendanceByStaff(Staff staff) {
        return attendanceRepository.findByStaff(staff);
    }
}
