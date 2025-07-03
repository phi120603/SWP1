package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.StaffRepository;
import com.example.swp.service.IssueService;
import com.example.swp.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/SWP/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private NotificationService notificationService;

    // Tạo mới Issue
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("issueRequest", new com.example.swp.dto.IssueRequest());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        return "create-issue";
    }

    @PostMapping("/create")
    public String createIssue(
            @ModelAttribute("issueRequest") @Valid com.example.swp.dto.IssueRequest issueRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        if (bindingResult.hasErrors()) {
            return "create-issue";
        }
        try {
            issueService.createIssue(issueRequest);
            model.addAttribute("success", "Tạo Issue thành công!");
            model.addAttribute("issueRequest", new com.example.swp.dto.IssueRequest());

            // Thông báo cho customer
            String email = (String) session.getAttribute("email");
            if (email != null) {
                Optional<Customer> customerOpt = customerRepository.findByEmail(email);
                customerOpt.ifPresent(c ->
                        notificationService.createNotification("Bạn vừa gửi yêu cầu hỗ trợ (Issue) thành công!", c)
                );
            }
        } catch (Exception e) {
            model.addAttribute("error", "Tạo Issue thất bại: " + e.getMessage());
        }
        return "create-issue";
    }

    // Danh sách Issue của customer (có thống kê + lọc)
    @GetMapping("/my-issues")
    public String viewCustomerIssues(
            Model model,
            HttpSession session,
            @RequestParam(value = "status", required = false) String status
    ) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("error", "Bạn chưa đăng nhập.");
            model.addAttribute("issues", Collections.emptyList());
            model.addAttribute("pendingCount", 0);
            model.addAttribute("progressCount", 0);
            model.addAttribute("resolvedCount", 0);
            model.addAttribute("closedCount", 0);
            model.addAttribute("totalCount", 0);
            model.addAttribute("status", status); // Để giữ trạng thái dropdown nếu có
            return "customer-issue-list";
        }
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy khách hàng!");
            model.addAttribute("issues", Collections.emptyList());
            model.addAttribute("pendingCount", 0);
            model.addAttribute("progressCount", 0);
            model.addAttribute("resolvedCount", 0);
            model.addAttribute("closedCount", 0);
            model.addAttribute("totalCount", 0);
            model.addAttribute("status", status);
            return "customer-issue-list";
        }

        List<Issue> issuesAll = issueService.getIssuesByCustomerId(customer.get().getId());

        // Đếm từng loại trạng thái
        long pendingCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.Pending).count();
        long progressCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.In_Progress).count();
        long resolvedCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.Resolved).count();
        long closedCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.Closed).count();

        // Lọc danh sách theo trạng thái nếu có status
        List<Issue> issues = issuesAll;
        if (status != null && !status.isEmpty()) {
            try {
                IssueStatus st = IssueStatus.valueOf(status);
                issues = issuesAll.stream()
                        .filter(i -> i.getStatus() == st)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // Nếu status truyền lên không đúng enum thì bỏ qua
            }
        }

        model.addAttribute("issues", issues);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("progressCount", progressCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("closedCount", closedCount);
        model.addAttribute("totalCount", issuesAll.size());
        model.addAttribute("status", status);

        return "customer-issue-list";
    }

    // Xem chi tiết Issue
    @GetMapping("/view")
    public String viewIssue(@RequestParam("id") int id, Model model) {
        Optional<Issue> issueOpt = issueService.getIssueById(id);
        if (issueOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy Issue!");
            return "issue-view";
        }
        model.addAttribute("issue", issueOpt.get());
        return "issue-view";
    }

    // Hiển thị form sửa Issue
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        Optional<Issue> issueOpt = issueService.getIssueById(id);
        if (issueOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy Issue!");
            return "issue-edit";
        }
        model.addAttribute("issue", issueOpt.get());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        return "issue-edit";
    }

    // Xử lý submit form sửa Issue
    @PostMapping("/edit")
    public String editIssue(@ModelAttribute("issue") @Valid Issue issue,
                            BindingResult bindingResult,
                            Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        if (bindingResult.hasErrors()) {
            return "issue-edit";
        }
        issueService.saveIssue(issue);
        model.addAttribute("success", "Cập nhật Issue thành công!");
        return "redirect:/SWP/issues/my-issues";
    }

    // Xóa Issue
    @PostMapping("/delete")
    public String deleteIssue(@RequestParam("id") int id, Model model) {
        issueService.deleteIssueById(id);
        return "redirect:/SWP/issues/my-issues";
    }

    // Trang tổng hợp cho admin/staff
    @GetMapping("/summary")
    public String summary(Model model) {
        long total = issueService.countAll();
        long choXuLy = issueService.countByStatus(IssueStatus.Pending);
        long dangXuLy = issueService.countByStatus(IssueStatus.In_Progress);
        long daGiaiQuyet = issueService.countByStatus(IssueStatus.Resolved);
        long daDong = issueService.countByStatus(IssueStatus.Closed);

        model.addAttribute("total", total);
        model.addAttribute("choXuLy", choXuLy);
        model.addAttribute("dangXuLy", dangXuLy);
        model.addAttribute("daGiaiQuyet", daGiaiQuyet);
        model.addAttribute("daDong", daDong);

        return "issue-summary";
    }
}
