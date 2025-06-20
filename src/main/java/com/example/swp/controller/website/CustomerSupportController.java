package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/SWP/support")
public class CustomerSupportController {

    @Autowired
    private CustomerRepository customerRepository;

    // Trang hỗ trợ chính
    @GetMapping
    public String supportPage() {
        return "customer-support";
    }

    // Form quên mật khẩu
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
            // Chuyển hướng đến trang detail
            return "redirect:/SWP/customers/" + customerId;
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng với thông tin đã nhập!");
            return "profile-access";
        }

    }

    // Xử lý quên mật khẩu
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
            // Có thể gửi mail thay vì show pass (nếu bảo mật), còn demo thì hiện pass luôn
            model.addAttribute("foundCustomer", customerOpt.get());
            return "show-password";
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng với thông tin đã nhập!");
            return "forgot-password";
        }
    }



}
