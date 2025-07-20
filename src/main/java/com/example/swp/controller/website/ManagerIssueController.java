package com.example.swp.controller.website;

import com.example.swp.entity.Issue;
import com.example.swp.entity.Manager;
import com.example.swp.enums.IssueStatus;
import com.example.swp.security.MyUserDetail;
import com.example.swp.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class ManagerIssueController {

    @Autowired
    private IssueService issueService;

    @GetMapping("/issue-list")
    public String showIssueList(@RequestParam(defaultValue = "0") int page, Model model) {
        List<Issue> allIssues = issueService.getAllIssues();

        int pageSize = 5;
        int totalIssues = allIssues.size();
        int totalPages = (int) Math.ceil((double) totalIssues / pageSize);

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalIssues);

        List<Issue> issues = allIssues.subList(startIndex, endIndex);

        model.addAttribute("issues", issues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalIssues", totalIssues);
        model.addAttribute("issueStatuses", IssueStatus.values());
        return "issue-list";
    }

    @GetMapping("/issue-list/view/{id}")
    public String viewIssue(@PathVariable int id, Model model) {
        Optional<Issue> issueOpt = issueService.getIssueById(id);
        if (issueOpt.isPresent()) {
            model.addAttribute("issue", issueOpt.get());
            return "issue-detail";
        }
        return "redirect:/admin/issue-list";
    }

    @PostMapping("/issue-list/update-status/{id}")
    public String updateIssueStatus(@PathVariable int id, @RequestParam("status") String status) {
        Optional<Issue> issueOpt = issueService.getIssueById(id);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            if ("RESOLVED".equals(status)) {
                issue.setStatus(IssueStatus.Resolved);
                issue.setResolved(true);
            } else if ("PENDING".equals(status)) {
                issue.setStatus(IssueStatus.Pending);
                issue.setResolved(false);
            }
            issueService.save(issue);
        }
        return "redirect:/admin/issue-list";
    }
}
