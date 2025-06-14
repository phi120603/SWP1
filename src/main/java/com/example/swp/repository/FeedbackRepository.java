package com.example.swp.repository;

import com.example.swp.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByCustomerId(int id);         // id của Customer
    List<Feedback> findByStaffStaffid(int staffid);  // staffid của Staff
}
