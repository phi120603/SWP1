package com.example.swp.repository;

import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
    List<Issue> findByCustomerId(int customerId);
    List<Issue> findByStatus(IssueStatus status);
    long countByStatus(IssueStatus status);
    long countByCustomerId(int customerId);
    long countByAssignedStaff_Staffid(int staffid); // Sửa lại dòng này
}
