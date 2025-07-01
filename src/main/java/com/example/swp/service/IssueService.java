package com.example.swp.service;

import com.example.swp.dto.IssueRequest;
import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface IssueService {
    // Lấy tất cả Issue
    List<Issue> getAllIssues();

    // Lấy Issue theo ID (Optional)
    Optional<Issue> getIssueById(int id);

    // Lấy Issue theo ID (bắt buộc, nếu không thấy thì throw)
    Issue getIssueByIdOrThrow(int id);

    // Tạo mới Issue
    Issue createIssue(IssueRequest issueRequest);

    // Lấy danh sách Issue theo customerId
    List<Issue> getIssuesByCustomerId(int customerId);

    // Lưu (Cập nhật) Issue
    Issue saveIssue(Issue issue);

    // Xóa Issue theo id
    void deleteIssueById(int id);

    // Đếm tổng số Issue
    long countAll();

    // Đếm số Issue theo trạng thái
    long countByStatus(IssueStatus status);
}
