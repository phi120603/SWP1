package com.example.swp.repository;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStaff(Staff staff);
}
