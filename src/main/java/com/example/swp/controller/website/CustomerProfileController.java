package com.example.swp.controller.website;

import com.example.swp.dto.ChangePasswordRequest;
import com.example.swp.dto.CustomerProfileUpdateRequest;
import com.example.swp.dto.ForgotPasswordRequest;
import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
import com.example.swp.service.NotificationService; // <-- thêm dòng này!
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class CustomerProfileController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService; // <-- thêm dòng này!

    // Hiển thị profile, luôn truyền đủ object cho các form!
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session,
                          @RequestParam(value = "tab", required = false, defaultValue = "profile") String tab) {
        String email = (String) session.getAttribute("email");
        CustomerProfileUpdateRequest customerProfile = new CustomerProfileUpdateRequest();
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();

        Customer customer = null;
        if (email != null) {
            customer = customerService.findByEmail(email);
            if (customer != null) {
                customerProfile.setFullname(customer.getFullname());
                customerProfile.setPhone(customer.getPhone());
                customerProfile.setEmail(customer.getEmail());
                customerProfile.setAddress(customer.getAddress());
            }
        }
        model.addAttribute("customerProfile", customerProfile);
        model.addAttribute("forgotPasswordRequest", forgotPasswordRequest);
        model.addAttribute("changePasswordRequest", changePasswordRequest);
        model.addAttribute("customer", customer);
        model.addAttribute("activeTab", tab);
        return "customer-profile";
    }

    // Cập nhật thông tin
    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute("customerProfile") @Valid CustomerProfileUpdateRequest form,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        Customer customer = email != null ? customerService.findByEmail(email) : null;

        // Luôn truyền lại attributes cho tất cả các tab!
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("customer", customer);
        model.addAttribute("activeTab", "update");

        if (bindingResult.hasErrors()) return "customer-profile";
        if (customer != null) {
            customer.setFullname(form.getFullname());
            customer.setPhone(form.getPhone());
            customer.setAddress(form.getAddress());
            customerService.save(customer);

            // ------> TẠO THÔNG BÁO Ở ĐÂY!
            notificationService.createNotification("Bạn vừa cập nhật thông tin cá nhân thành công.", customer);

            Customer updated = customerService.findByEmail(email);
            model.addAttribute("customerProfile", form);
            model.addAttribute("customer", updated);
            model.addAttribute("success", "Cập nhật thành công!");
            return "customer-profile";
        }
        model.addAttribute("error", "Không xác định được khách hàng.");
        return "customer-profile";
    }

    // Quên mật khẩu
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @ModelAttribute("forgotPasswordRequest") @Valid ForgotPasswordRequest form,
            BindingResult bindingResult,
            Model model
    ) {
        model.addAttribute("customerProfile", new CustomerProfileUpdateRequest());
        model.addAttribute("forgotPasswordRequest", form);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("customer", null);
        model.addAttribute("activeTab", "forgot");

        if (bindingResult.hasErrors()) return "customer-profile";
        Customer customer = customerService.findByEmail(form.getEmail());
        if (customer == null) {
            bindingResult.rejectValue("email", "notfound", "Email này không tồn tại!");
            return "customer-profile";
        }

        // TODO: Xử lý gửi mail
        model.addAttribute("message", "Hướng dẫn đặt lại mật khẩu đã được gửi!");
        return "customer-profile";
    }

    // Đổi mật khẩu (submit trên tab đổi mật khẩu)
    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute("changePasswordRequest") @Valid ChangePasswordRequest form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        String email = (String) session.getAttribute("email");
        model.addAttribute("customerProfile", new CustomerProfileUpdateRequest());
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        model.addAttribute("changePasswordRequest", form);
        model.addAttribute("activeTab", "changePassword");
        Customer customer = email != null ? customerService.findByEmail(email) : null;
        model.addAttribute("customer", customer);

        if (email == null) {
            model.addAttribute("error", "Bạn chưa đăng nhập.");
            return "customer-profile";
        }
        if (bindingResult.hasErrors()) return "customer-profile";
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "match", "Xác nhận mật khẩu không khớp!");
            return "customer-profile";
        }
        if (customer == null) {
            model.addAttribute("error", "Không tìm thấy thông tin khách hàng.");
            return "customer-profile";
        }

        // So sánh mã hóa
        if (!passwordEncoder.matches(form.getOldPassword(), customer.getPassword())) {
            bindingResult.rejectValue("oldPassword", "invalid", "Mật khẩu cũ không đúng!");
            return "customer-profile";
        }

        customer.setPassword(passwordEncoder.encode(form.getNewPassword()));
        customerService.save(customer);

        // ------> TẠO THÔNG BÁO Ở ĐÂY (tuỳ chọn)
        notificationService.createNotification("Bạn vừa đổi mật khẩu thành công.", customer);

        model.addAttribute("success", "Đổi mật khẩu thành công!");
        return "customer-profile";
    }
}
