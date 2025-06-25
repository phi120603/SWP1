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
public class CustomerProfileController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmailService emailService;

    // Hiển thị trang profile (cả 3 chức năng trong 1 file)
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            Customer customer = customerService.findByEmail(email);
            model.addAttribute("customer", customer);
        }
        return "customer-profile"; // Tên file html (templates/customer-profile.html)
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("customer") Customer customer, Model model, HttpSession session) {
        // Giữ lại email gốc từ session nếu bị chỉnh sửa (nên để trường email readonly trong form)
        String email = (String) session.getAttribute("email");
        if (email != null) {
            Customer current = customerService.findByEmail(email);
            if (current != null) {
                customer.setId(current.getId()); // Đảm bảo đúng ID
                customer.setPassword(current.getPassword()); // Không sửa password
                customer.setRoleName(current.getRoleName());
                Customer updated = customerService.save(customer);
                model.addAttribute("customer", updated);
                model.addAttribute("success", "Cập nhật thành công!");
                return "customer-profile";
            }
        }
        model.addAttribute("error", "Không xác định được khách hàng.");
        return "customer-profile";
    }

    // Quên mật khẩu: gửi lại mật khẩu hoặc đường link reset về email
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, Model model, HttpSession session) {
        Customer customer = customerService.findByEmail(email);
        if (customer == null) {
            model.addAttribute("error", "Email này không tồn tại trong hệ thống.");
        } else {
            String newPassword = "123456"; // Hoặc random thực tế
            customer.setPassword(newPassword);
            customerService.save(customer);
            emailService.sendEmail(email, "QVL Storage - Reset mật khẩu",
                    "Mật khẩu mới của bạn: " + newPassword);
            model.addAttribute("message", "Mật khẩu mới đã được gửi đến email của bạn.");
        }
        // Truyền lại customer cho tab profile (nếu đã login thì lấy từ session)
        String currentEmail = (String) session.getAttribute("email");
        if (currentEmail != null) {
            Customer loggedIn = customerService.findByEmail(currentEmail);
            model.addAttribute("customer", loggedIn);
        }
        return "customer-profile";
    }
}
