package com.example.swp.service;

import com.example.swp.entity.Feedback;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    List<Feedback> getAllFeedbacks();
    Optional<Feedback> getFeedbackById(int id);
    List<Feedback> findByStaffId(int staffId);        // Optional
    List<Feedback> findByCustomerId(int customerId);  // Optional
    Feedback save(Feedback feedback);
}
