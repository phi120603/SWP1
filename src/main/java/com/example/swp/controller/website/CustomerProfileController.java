package com.example.swp.controller.website;

import com.example.swp.dto.CustomerProfileUpdateRequest;
import com.example.swp.dto.ForgotPasswordRequest;
import com.example.swp.entity.Customer;
import com.example.swp.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerProfileController {

    @Autowired
    private CustomerService customerService;

    // Hiển thị trang profile (tab mặc định: xem hồ sơ)
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session,
                          @RequestParam(value = "tab", required = false, defaultValue = "profile") String tab) {
        String email = (String) session.getAttribute("email");
        CustomerProfileUpdateRequest customerProfile = new CustomerProfileUpdateRequest();
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
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
        // Luôn luôn set đủ model attributes
        model.addAttribute("customerProfile", customerProfile);
        model.addAttribute("forgotPasswordRequest", forgotPasswordRequest);
        model.addAttribute("customer", customer);
        model.addAttribute("activeTab", tab); // tab nào đang active
        return "customer-profile";
    }

    // Cập nhật thông tin profile
    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute("customerProfile") @Valid CustomerProfileUpdateRequest form,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        Customer customer = null;
        if (email != null) {
            customer = customerService.findByEmail(email);
        }
        // Luôn truyền lại các model attribute
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        model.addAttribute("customer", customer);
        model.addAttribute("activeTab", "update");

        if (bindingResult.hasErrors()) {
            return "customer-profile";
        }
        if (customer != null) {
            customer.setFullname(form.getFullname());
            customer.setPhone(form.getPhone());
            customer.setAddress(form.getAddress());
            customerService.save(customer);
            // Lấy lại customer mới nhất
            Customer updated = customerService.findByEmail(email);
            model.addAttribute("customerProfile", form); // giữ dữ liệu user vừa nhập
            model.addAttribute("customer", updated);
            model.addAttribute("success", "Cập nhật thành công!");
            return "customer-profile";
        }
        model.addAttribute("error", "Không xác định được khách hàng.");
        return "customer-profile";
    }

    // Xử lý quên mật khẩu
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @ModelAttribute("forgotPasswordRequest") @Valid ForgotPasswordRequest form,
            BindingResult bindingResult,
            Model model
    ) {
        // Luôn truyền lại các model attribute để không bị trắng tab
        model.addAttribute("customerProfile", new CustomerProfileUpdateRequest());
        model.addAttribute("forgotPasswordRequest", form); // giữ lại email user vừa nhập
        model.addAttribute("customer", null);
        model.addAttribute("activeTab", "forgot");

        if (bindingResult.hasErrors()) {
            return "customer-profile";
        }

        Customer customer = customerService.findByEmail(form.getEmail());
        if (customer == null) {
            bindingResult.rejectValue("email", "notfound", "Email này không tồn tại!");
            return "customer-profile";
        }

        // TODO: Xử lý gửi mail đặt lại mật khẩu ở đây...

        model.addAttribute("message", "Hướng dẫn đặt lại mật khẩu đã được gửi!");
        return "customer-profile";
    }
}
