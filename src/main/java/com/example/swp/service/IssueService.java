package com.example.swp.service;

import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface IssueService {
    List<Issue> getAllIssues();
    Optional<Issue> getIssueById(int id);
    Issue createIssue(IssueRequest issueRequest);
    List<Issue> getIssuesByCustomerId(int customerId);
    Issue getIssueByIdOrThrow(int id);
    Issue saveIssue(Issue issue);
    void deleteIssueById(int id);
    long countAll();
    long countByStatus(IssueStatus status);

    // *** THÊM DÒNG NÀY ***
    List<Issue> searchAndFilterIssues(String search, String status);


    void updateAssignedStaffAndStatus(int id, int staffId, Boolean resolved);

}
