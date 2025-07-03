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

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByStaffId(Long staffId) {
        return attendanceRepository.findByStaff_Staffid(staffId);
    }

    @Override
    public List<Attendance> getAttendanceByDateRange(LocalDateTime from, LocalDateTime to) {
        return attendanceRepository.findByCheckInTimeBetween(from, to);
    }

    @Override
    public List<Attendance> getAttendanceByStaffIdAndDateRange(Long staffId, LocalDateTime from, LocalDateTime to) {
        return attendanceRepository.findByStaff_StaffidAndCheckInTimeBetween(staffId, from, to);
    }
}
