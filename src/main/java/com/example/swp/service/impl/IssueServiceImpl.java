package com.example.swp.service.impl;

import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Issue;
import com.example.swp.entity.Staff;
import com.example.swp.enums.IssueStatus;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.IssueRepository;
import com.example.swp.repository.StaffRepository;
import com.example.swp.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Optional<Issue> getIssueById(int id) {
        return issueRepository.findById(id);
    }

    @Override
    public Issue createIssue(IssueRequest issueRequest) {
        Customer customer = null;
        if (issueRequest.getCustomerId() != null) {
            customer = customerRepository.findById(issueRequest.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id " + issueRequest.getCustomerId()));
        }

        Staff staff = staffRepository.findById(issueRequest.getAssignedStaffId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff với id " + issueRequest.getAssignedStaffId()));

        Issue issue = new Issue();
        issue.setSubject(issueRequest.getSubject());
        issue.setDescription(issueRequest.getDescription());
        issue.setCustomer(customer); // có thể null cho vấn đề nội bộ
        issue.setAssignedStaff(staff);
        issue.setCreatedDate(new Date());
        issue.setResolved(false);
        issue.setCreatedByType("CUSTOMER");
        issue.setStatus(IssueStatus.Pending);

        return issueRepository.save(issue);
    }

    @Override
    public List<Issue> getIssuesByCustomerId(int customerId) {
        return List.of();
    }

    @Override
    public Issue getIssueByIdOrThrow(int id) {
        return null;
    }

    @Override
    public Issue saveIssue(Issue issue) {
        return null;
    }

    @Override
    public void deleteIssueById(int id) {

    }

    @Override
    public long countAll() {
        return 0;
    }

    @Override
    public long countByStatus(IssueStatus status) {
        return 0;
    }

    @Override
    public List<Issue> searchAndFilterIssues(String search, String status) {
        return List.of();
    }

    @Override
    public void updateAssignedStaffAndStatus(int id, int staffId, Boolean resolved) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy issue với id " + id));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff với id " + staffId));
        issue.setAssignedStaff(staff);
        issue.setResolved(resolved);
        issueRepository.save(issue);
    }

    @Override
    public void save(Issue issue) {
        issueRepository.save(issue);
    }
}
