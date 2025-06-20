package com.example.swp.service;

import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Issue;

import java.util.List;
import java.util.Optional;

public interface IssueService {
    List<Issue> getAllIssues();
    Optional<Issue> getIssueById(int id);
    Issue createIssue(IssueRequest issueRequest);
}
