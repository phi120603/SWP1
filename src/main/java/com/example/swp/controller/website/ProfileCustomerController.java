package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
import com.example.swp.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileCustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmailService emailService;

    // Hiển thị trang profile (cả 3 chức năng trong 1 file)
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        // Lấy email khách hàng từ session (tùy bạn lưu session thế nào)
        String email = (String) session.getAttribute("email");
        if (email != null) {
            Customer customer = customerService.findByEmail(email);
            model.addAttribute("customer", customer);
        }
        return "profile-customer";
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("customer") Customer customer, Model model) {
        Customer updated = customerService.save(customer);
        model.addAttribute("customer", updated);
        model.addAttribute("success", "Cập nhật thành công!");
        return "profile-customer";
    }

    // Quên mật khẩu: gửi lại mật khẩu hoặc đường link reset về email
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        Customer customer = customerService.findByEmail(email);
        if (customer == null) {
            model.addAttribute("error", "Email này không tồn tại trong hệ thống.");
        } else {
            // Tạo mật khẩu mới random hoặc gửi link reset (demo: gửi mật khẩu mới luôn)
            String newPassword = "123456"; // Tạo random ở đây cho thực tế
            customer.setPassword(newPassword);
            customerService.save(customer);
            emailService.sendEmail(email, "QVL Storage - Reset mật khẩu",
                    "Mật khẩu mới của bạn: " + newPassword);
            model.addAttribute("message", "Mật khẩu mới đã được gửi đến email của bạn.");
        }
        return "profile-customer";
    }
}
