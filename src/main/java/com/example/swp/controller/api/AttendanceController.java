package com.example.swp.controller.api;

import com.example.swp.entity.Attendance;
import com.example.swp.entity.Staff;
import com.example.swp.repository.StaffRepository;
import com.example.swp.service.AttendanceService;
import com.example.swp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StaffRepository staffReponsitory;

    @Autowired
    private EmailService emailService;

    private final Map<Integer, String> otpCache = new ConcurrentHashMap<>();

    private final Random random = new Random(); // Thêm vào đây

    @PostMapping("/request-checkin-otp")
    public String requestCheckinOtp(Principal principal) {
        Staff staff = getCurrentStaff(principal);
        String otp = String.format("%06d", random.nextInt(999999));
        otpCache.put(staff.getStaffid(), otp);

        emailService.sendOtpEmail(staff.getEmail(), otp); // Gửi qua service sẵn có

        return "OTP đã gửi đến email.";
    }

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

//    @PostMapping("/checkin")
//    public Attendance checkIn(Principal principal) {
//        Staff staff = getCurrentStaff(principal);
//        return attendanceService.checkIn(staff);
//    }

    @PostMapping("/confirm-checkin-otp")
    public Attendance confirmCheckinOtp(@RequestParam String otp, Principal principal) {
        Staff staff = getCurrentStaff(principal);
        String cachedOtp = otpCache.get(staff.getStaffid());

        if (cachedOtp == null) {
            throw new RuntimeException("Chưa yêu cầu mã OTP hoặc mã đã hết hạn.");
        }

        if (!cachedOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác.");
        }

        otpCache.remove(staff.getStaffid()); // Xóa OTP sau khi dùng
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
