package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.SupportActivity;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.SupportActivityRepository;
import com.example.swp.validate.ForgotPasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/SWP/support")
public class CustomerSupportController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SupportActivityRepository supportActivityRepository;

    @GetMapping
    public String supportPage(Model model) {
        List<SupportActivity> activities = supportActivityRepository.findTop10ByOrderByActivityTimeDesc();
        model.addAttribute("activities", activities);
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
            // Ghi nhận lịch sử sử dụng
            SupportActivity activity = new SupportActivity();
            activity.setActivityType("Profile Access");
            activity.setStatus("Resolved");
            activity.setActivityTime(new Date());
            activity.setCustomer(customerOpt.get());
            supportActivityRepository.save(activity);

            int customerId = customerOpt.get().getId();
            return "redirect:/SWP/customers/" + customerId;
        } else {
            // Ghi nhận lịch sử thất bại (nếu muốn)
            SupportActivity activity = new SupportActivity();
            activity.setActivityType("Profile Access");
            activity.setStatus("Failed");
            activity.setActivityTime(new Date());
            supportActivityRepository.save(activity);

            model.addAttribute("error", "Không tìm thấy khách hàng với thông tin đã nhập!");
            return "profile-access";
        }

    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("fullname") String fullname,
            @RequestParam("id_citizen") String idCitizen,
            Model model) {

        // Validate: Kiểm tra rỗng
        Map<String, String> errors = ForgotPasswordValidator.validate(fullname, idCitizen);

        // Nếu bị thiếu trường nào, trả lỗi luôn
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("fullname", fullname);
            model.addAttribute("id_citizen", idCitizen);
            return "forgot-password";
        }

        // Kiểm tra có trong DB không
        Optional<Customer> customerOpt = customerRepository
                .findAll()
                .stream()
                .filter(c -> c.getFullname().equalsIgnoreCase(fullname.trim())
                        && c.getId_citizen().equals(idCitizen.trim()))
                .findFirst();

        if (customerOpt.isEmpty()) {
            errors.put("global", "Không tìm thấy họ tên và số căn cước phù hợp!");
            // Hoặc bạn có thể gán lỗi này vào từng trường:
            errors.put("fullname", "Không tìm thấy họ tên và số căn cước phù hợp!");
            errors.put("id_citizen", "Không tìm thấy họ tên và số căn cước phù hợp!");
            model.addAttribute("errors", errors);
            model.addAttribute("fullname", fullname);
            model.addAttribute("id_citizen", idCitizen);
            return "forgot-password";
        }

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
                // Ghi nhận lịch sử thành công
                SupportActivity activity = new SupportActivity();
                activity.setActivityType("Password Recovery");
                activity.setStatus("Resolved");
                activity.setActivityTime(new Date());
                activity.setCustomer(customer);
                supportActivityRepository.save(activity);

                model.addAttribute("success", "Mật khẩu đã được gửi tới email: " + maskEmail(customer.getEmail()));
            } catch (Exception ex) {
                // Ghi nhận lịch sử thất bại
                SupportActivity activity = new SupportActivity();
                activity.setActivityType("Password Recovery");
                activity.setStatus("Failed");
                activity.setActivityTime(new Date());
                activity.setCustomer(customer);
                supportActivityRepository.save(activity);

                model.addAttribute("error", "Gửi email thất bại. Vui lòng thử lại sau!");
            }
        } else {
            // Ghi nhận lịch sử thất bại
            SupportActivity activity = new SupportActivity();
            activity.setActivityType("Password Recovery");
            activity.setStatus("Failed");
            activity.setActivityTime(new Date());
            supportActivityRepository.save(activity);

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
