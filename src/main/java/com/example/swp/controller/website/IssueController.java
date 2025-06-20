package com.example.swp.controller.website;

import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Issue;
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
    public String createIssue(@ModelAttribute IssueRequest issueRequest, Model model) {
        try {
            issueService.createIssue(issueRequest);
            model.addAttribute("success", "Tạo Issue thành công!");
            model.addAttribute("issueRequest", new IssueRequest()); // reset form
        } catch (Exception e) {
            model.addAttribute("error", "Tạo Issue thất bại: " + e.getMessage());
        }
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        return "create-issue";
    }

    @GetMapping("/report")
    public String showStaffReport(Model model) {
        List<Issue> issues = issueService.getAllIssues();
        model.addAttribute("issues", issues);
        return "staff-report";
    }

}
