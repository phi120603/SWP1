package com.example.swp.controller.website;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Issue;
import com.example.swp.entity.Staff;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.StaffReponsitory;
import com.example.swp.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@Controller
@RequestMapping("/SWP/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffReponsitory staffRepository;

    // Hiển thị form tạo mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("issueRequest", new IssueRequest());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        return "create-issue";
    }

    // Nhận form submit
    @PostMapping("/create")
    public String createIssue(
            @ModelAttribute("issueRequest") @Valid IssueRequest issueRequest,
            BindingResult bindingResult,
            Model model) {

        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());

        if (bindingResult.hasErrors()) {
            // Có lỗi validate: trả lại form và hiển thị lỗi cạnh trường nhập!
            return "create-issue";
        }

        try {
            issueService.createIssue(issueRequest);
            model.addAttribute("success", "Tạo Issue thành công!");
            model.addAttribute("issueRequest", new IssueRequest()); // reset form
        } catch (Exception e) {
            model.addAttribute("error", "Tạo Issue thất bại: " + e.getMessage());
        }
        return "create-issue";
    }

    @GetMapping("/report")
    public String showStaffReport(Model model) {
        List<Issue> issues = issueService.getAllIssues();
        model.addAttribute("issues", issues);
        return "staff-report";
    }
    @GetMapping("/my-issues")
    public String viewCustomerIssues(Model model, HttpSession session) {
        // Lấy ID customer từ session, hoặc nếu bạn lưu email thì tìm theo email:
        String email = (String) session.getAttribute("email");
        if (email == null) {
            // Nếu chưa đăng nhập, redirect về trang login hoặc báo lỗi
            return "redirect:/login";
        }
        // Tìm customer theo email
        var customer = customerRepository.findByEmail(email);
        if (customer == null) {
            model.addAttribute("error", "Không xác định được khách hàng!");
            return "customer-issue-list";
        }
        // Lấy các issue của customer
        List<Issue> issues = issueService.getIssuesByCustomerId(customer.get().getId());
        model.addAttribute("issues", issues);
        return "customer-issue-list"; // Tạo file html mới này
    }

    @GetMapping("/staff-send-report")
    public String showSendReportForm(Model model) {
        List<Issue> issues = issueService.getAllIssues();
        model.addAttribute("issues", issues);
        return "staff-send-report";
    }

    @PostMapping("/staff-send-report")
    public String sendReport(
            @RequestParam(required = false) List<Long> issueIds,
            @RequestParam(required = false) String customReport,
            Model model
    ) {
        try {
            model.addAttribute("success", "Gửi báo cáo thành công!");
            // Reset form:
            model.addAttribute("issues", issueService.getAllIssues());
            model.addAttribute("customReport", ""); // hoặc model.addAttribute("customReport", null);

            return "staff-send-report";
        } catch (Exception e) {
            model.addAttribute("error", "Gửi báo cáo thất bại: " + e.getMessage());
            model.addAttribute("issues", issueService.getAllIssues());
            return "staff-send-report";
        }
    }

    @GetMapping("/{id}/detail")
    public String issueDetail(@PathVariable("id") int id, Model model) {
        Issue issue = issueService.getIssueById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy issue!"));
        List<Staff> staffs = staffRepository.findAll();
        model.addAttribute("issue", issue);
        model.addAttribute("staffs", staffs);
        return "staff-report-detail";
    }

    @PostMapping("/{id}/detail")
    public String updateIssueDetail(
            @PathVariable("id") int id,
            @RequestParam("assignedStaffId") int assignedStaffId,
            @RequestParam("resolved") Boolean resolved,
            Model model) {
        issueService.updateAssignedStaffAndStatus(id, assignedStaffId, resolved);

        Issue issue = issueService.getIssueById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy issue!"));
        List<Staff> staffs = staffRepository.findAll();
        model.addAttribute("issue", issue);
        model.addAttribute("staffs", staffs);
        model.addAttribute("success", "Cập nhật thành công!");
        return "staff-report-detail";
    }

}
