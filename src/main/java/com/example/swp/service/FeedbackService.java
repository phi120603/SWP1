package com.example.swp.service;

import com.example.swp.entity.Feedback;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    List<Feedback> getAllFeedbacks();
    Optional<Feedback> getFeedbackById(int id);
    List<Feedback> findByCustomerId(int customerId);
    List<Feedback> findByStorageId(int storageId);   // thêm dòng này
    Feedback save(Feedback feedback);
}

