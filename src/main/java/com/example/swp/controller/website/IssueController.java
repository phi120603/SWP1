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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
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
            HttpSession session,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        String email = (String) session.getAttribute("email");
        if (email != null && customerRepository.findByEmail(email).isPresent()) {
            return "redirect:/SWP/issues/my-issues"; // chặn customer
        }

        Page<Issue> issuePage = issueService.searchAndFilterIssuesPaginated(search, status, page, size);

        model.addAttribute("issuePage", issuePage);
        model.addAttribute("issues", issuePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", issuePage.getTotalPages());

        model.addAttribute("pendingCount", issueService.countByStatus(IssueStatus.Pending));
        model.addAttribute("progressCount", issueService.countByStatus(IssueStatus.In_Progress));
        model.addAttribute("resolvedCount", issueService.countByStatus(IssueStatus.Resolved));
        model.addAttribute("closedCount", issueService.countByStatus(IssueStatus.Closed));
        model.addAttribute("totalCount", issueService.countAll());

        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "customer-issue-list";
    }


    // ----------- Tạo mới Issue -----------
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("issueRequest", new IssueRequest());
        model.addAttribute("customers", customerRepository.findAll());
        return "create-issue"; // Đã loại bỏ staffs
    }


    // Nhận form submit
    @PostMapping("/create")
    public String createIssue(
            @ModelAttribute("issueRequest") @Valid IssueRequest issueRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        model.addAttribute("customers", customerRepository.findAll());

        if (bindingResult.hasErrors()) {
            return "create-issue";
        }

        try {
            issueService.createIssue(issueRequest);
            model.addAttribute("success", "Tạo Issue thành công!");
            model.addAttribute("issueRequest", new IssueRequest());

            // Thông báo cho customer (nếu có đăng nhập)
            String email = (String) session.getAttribute("email");
            if (email != null) {
                customerRepository.findByEmail(email)
                        .ifPresent(c -> notificationService.createNotification("Bạn vừa gửi yêu cầu hỗ trợ (Issue) thành công!", c));
            }

        } catch (Exception e) {
            model.addAttribute("error", "Tạo Issue thất bại: " + e.getMessage());
        }

        return "create-issue";
    }

    @GetMapping("/report")
    public String showStaffReport(Model model) {
        List<Issue> issues = issueService.getAllIssues();
        if (issues == null) {
            issues = new ArrayList<>();
        }
        model.addAttribute("issues", issues);
        return "staff-report";
    }
    @GetMapping("/my-issues")
    public String viewCustomerIssues(
            Model model,
            HttpSession session,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("error", "Bạn chưa đăng nhập.");
            return "customer-issue-list";
        }

        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy khách hàng!");
            return "customer-issue-list";
        }

        Page<Issue> issuePage = issueService.getCustomerIssuesPaginated(
                customer.get().getId(), status, page, size);

        List<Issue> allIssues = issueService.getIssuesByCustomerId(customer.get().getId());

        model.addAttribute("issuePage", issuePage);
        model.addAttribute("issues", issuePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", issuePage.getTotalPages());

        // 🧠 Thêm lại thống kê
        long pendingCount = allIssues.stream().filter(i -> i.getStatus() == IssueStatus.Pending).count();
        long progressCount = allIssues.stream().filter(i -> i.getStatus() == IssueStatus.In_Progress).count();
        long resolvedCount = allIssues.stream().filter(i -> i.getStatus() == IssueStatus.Resolved).count();
        long closedCount = allIssues.stream().filter(i -> i.getStatus() == IssueStatus.Closed).count();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("progressCount", progressCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("closedCount", closedCount);
        model.addAttribute("totalCount", allIssues.size());

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
