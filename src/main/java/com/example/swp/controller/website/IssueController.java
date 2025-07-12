package com.example.swp.controller.website;

import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;
import com.example.swp.entity.Staff;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.StaffRepository;
import com.example.swp.service.IssueService;
import com.example.swp.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // ----------- Trang danh sách issue (cho admin/staff, search + filter) -----------
    @GetMapping
    public String listAllIssues(
            Model model,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status
    ) {
        List<Issue> issues = issueService.searchAndFilterIssues(search, status);

        long pendingCount = issueService.countByStatus(IssueStatus.Pending);
        long progressCount = issueService.countByStatus(IssueStatus.In_Progress);
        long resolvedCount = issueService.countByStatus(IssueStatus.Resolved);
        long closedCount = issueService.countByStatus(IssueStatus.Closed);
        long totalCount = issueService.countAll();

        model.addAttribute("issues", issues);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("progressCount", progressCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("closedCount", closedCount);
        model.addAttribute("totalCount", totalCount);

        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "customer-issue-list";
    }

    // --------------- CREATE FORM ---------------
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("issueRequest", new IssueRequest());
        model.addAttribute("customers", customerRepository.findAll());

        // Lấy thông tin xác thực từ Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOrStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("MANAGER") || a.getAuthority().equals("STAFF"));

        // Nếu là admin/staff thì hiển thị danh sách staffs
        if (isAdminOrStaff) {
            model.addAttribute("staffs", staffRepository.findAll());
        } else {
            model.addAttribute("staffs", Collections.emptyList());
        }

        return "create-issue";
    }

    @PostMapping("/create")
    public String createIssue(
            @ModelAttribute("issueRequest") @Valid IssueRequest issueRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        model.addAttribute("customers", customerRepository.findAll());

        // Xác thực user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOrStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("MANAGER") || a.getAuthority().equals("STAFF"));

        if (isAdminOrStaff) {
            model.addAttribute("staffs", staffRepository.findAll());
        } else {
            model.addAttribute("staffs", Collections.emptyList());
        }

        if (bindingResult.hasErrors()) {
            return "create-issue";
        }

        try {
            issueService.createIssue(issueRequest);
            model.addAttribute("success", "Tạo Issue thành công!");
            model.addAttribute("issueRequest", new IssueRequest());

            // Thông báo cho khách nếu có đăng nhập
            String email = (String) session.getAttribute("email");
            if (email != null) {
                customerRepository.findByEmail(email).ifPresent(c ->
                        notificationService.createNotification("Bạn vừa gửi yêu cầu hỗ trợ thành công!", c));
            }
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
            model.addAttribute("status", status);
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

        // Đếm trạng thái
        long pendingCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.Pending).count();
        long progressCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.In_Progress).count();
        long resolvedCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.Resolved).count();
        long closedCount = issuesAll.stream().filter(i -> i.getStatus() == IssueStatus.Closed).count();

        // Lọc theo trạng thái nếu có
        List<Issue> issues = issuesAll;
        if (status != null && !status.isEmpty()) {
            try {
                IssueStatus st = IssueStatus.valueOf(status);
                issues = issuesAll.stream()
                        .filter(i -> i.getStatus() == st)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // ignore nếu status không hợp lệ
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

    // ----------- Xem chi tiết Issue -----------
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

    // ----------- Hiển thị form sửa Issue -----------
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

    // ----------- Xóa Issue -----------
    @PostMapping("/delete")
    public String deleteIssue(@RequestParam("id") int id, Model model) {
        issueService.deleteIssueById(id);
        return "redirect:/SWP/issues/my-issues";
    }

    // ----------- Trang tổng hợp Issue -----------
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
