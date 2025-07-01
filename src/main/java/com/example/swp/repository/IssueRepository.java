package com.example.swp.repository;

import com.example.swp.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
    List<Issue> findByCustomerId(int customerId);
}
