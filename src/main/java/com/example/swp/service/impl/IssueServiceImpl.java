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
    public Issue getIssueByIdOrThrow(int id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Issue với id " + id));
    }

    @Override
    public Issue createIssue(IssueRequest issueRequest) {
        Customer customer = customerRepository.findById(issueRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id " + issueRequest.getCustomerId()));
        Staff staff = staffRepository.findById(issueRequest.getAssignedStaffId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff với id " + issueRequest.getAssignedStaffId()));

        Issue issue = new Issue();
        issue.setSubject(issueRequest.getSubject());
        issue.setDescription(issueRequest.getDescription());
        issue.setCustomer(customer);
        issue.setAssignedStaff(staff);
        issue.setCreatedDate(new Date());
        issue.setResolved(false);
        issue.setStatus(IssueStatus.Pending);

        return issueRepository.save(issue);
    }

    @Override
    public List<Issue> getIssuesByCustomerId(int customerId) {
        return issueRepository.findByCustomerId(customerId);
    }

    @Override
    public Issue saveIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public void deleteIssueById(int id) {
        issueRepository.deleteById(id);
    }

    @Override
    public long countAll() {
        return issueRepository.count();
    }

    @Override
    public long countByStatus(IssueStatus status) {
        return issueRepository.countByStatus(status);
    }
    @Override
    public List<Issue> searchAndFilterIssues(String search, String status) {
        if ((search == null || search.isBlank()) && (status == null || status.isBlank()))
            return issueRepository.findAll();

        IssueStatus st = null;
        if (status != null && !status.isBlank()) {
            try { st = IssueStatus.valueOf(status); } catch (Exception ignored) {}
        }

        if (search != null && !search.isBlank() && st != null)
            return issueRepository.searchByKeywordAndStatus(search, st);
        if (search != null && !search.isBlank())
            return issueRepository.searchByKeyword(search);
        if (st != null)
            return issueRepository.findByStatus(st);

        return issueRepository.findAll();
    }

}
