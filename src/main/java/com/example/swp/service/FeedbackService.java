package com.example.swp.service;

import com.example.swp.entity.Feedback;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    Feedback createOrUpdateFeedback(int storageId, int customerId, String content, int rating);
    Feedback createFeedback(int storageId, int customerId, String content, int rating);
    Optional<Feedback> findByCustomerAndStorage(Customer customer, Storage storage);
    boolean existsByCustomerAndStorage(Customer customer, Storage storage);

    List<Feedback> getAllFeedbacks();
    Feedback getFeedbackById(int id);
    void deleteFeedback(int id);
}
