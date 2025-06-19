package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/SWP/support")
public class CustomerSupportController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JavaMailSender mailSender; // Thêm dòng này

    @GetMapping
    public String supportPage() {
        return "customer-support";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/profile-access")
    public String profileAccessPage() {
        return "profile-access";
    }

    @PostMapping("/profile-access")
    public String handleProfileAccess(
            @RequestParam("fullname") String fullname,
            @RequestParam("id_citizen") String idCitizen,
            Model model) {

        Optional<Customer> customerOpt = customerRepository
                .findAll()
                .stream()
                .filter(c -> c.getFullname().equalsIgnoreCase(fullname.trim()) &&
                        c.getId_citizen().equals(idCitizen.trim()))
                .findFirst();

        if (customerOpt.isPresent()) {
            int customerId = customerOpt.get().getId();
            return "redirect:/SWP/customers/" + customerId;
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng với thông tin đã nhập!");
            return "profile-access";
        }

    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("fullname") String fullname,
            @RequestParam("id_citizen") String idCitizen,
            Model model) {

        Optional<Customer> customerOpt = customerRepository
                .findAll()
                .stream()
                .filter(c -> c.getFullname().equalsIgnoreCase(fullname.trim()) &&
                        c.getId_citizen().equals(idCitizen.trim()))
                .findFirst();

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // Gửi email chứa mật khẩu tới khách hàng
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(customer.getEmail());
            message.setSubject("Password Recovery - QVL Storage");
            message.setText(
                    "Xin chào " + customer.getFullname() + ",\n\n"
                            + "Bạn vừa yêu cầu lấy lại mật khẩu tại QVL Storage.\n"
                            + "Mật khẩu của bạn là: " + customer.getPassword() + "\n\n"
                            + "Vui lòng đổi mật khẩu sau khi đăng nhập!\n\n"
                            + "Trân trọng,\nQVL Storage Support"
            );
            try {
                mailSender.send(message);
                model.addAttribute("success", "Mật khẩu đã được gửi tới email: " + maskEmail(customer.getEmail()));
            } catch (Exception ex) {
                model.addAttribute("error", "Gửi email thất bại. Vui lòng thử lại sau!");
            }
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng với thông tin đã nhập!");
        }
        return "forgot-password";
    }

    // Hàm che bớt email cho thông báo hiển thị (vd: ab***@gmail.com)
    private String maskEmail(String email) {
        int idx = email.indexOf("@");
        if (idx <= 2) return "***" + email.substring(idx);
        return email.substring(0, 2) + "***" + email.substring(idx);
    }
}
