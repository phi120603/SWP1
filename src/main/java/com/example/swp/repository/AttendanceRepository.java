package com.example.swp.repository;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // Lấy danh sách chấm công của 1 staff
    List<Attendance> findByStaff(Staff staff);

    // Lấy danh sách chấm công theo staffId (nếu cần)
    List<Attendance> findByStaff_Staffid(Long staffid);

    // Lọc chấm công theo khoảng ngày check-in
    List<Attendance> findByCheckInTimeBetween(LocalDateTime from, LocalDateTime to);

    // Lọc chấm công theo staff và khoảng ngày (tùy nhu cầu)
    List<Attendance> findByStaff_StaffidAndCheckInTimeBetween(Long staffid, LocalDateTime from, LocalDateTime to);
}
