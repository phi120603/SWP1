package com.example.swp.service;

import com.example.swp.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    List<Feedback> getAllFeedbacks();
    Feedback getFeedbackById(int id);
    Feedback createFeedback(Feedback feedback);
    void deleteFeedback(int id);
}
