package com.example.swp.service.impl;

import com.example.swp.entity.Feedback;
import com.example.swp.repository.FeedbackRepository;
import com.example.swp.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getFeedbackById(int id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> findByStaffId(int staffId) {
        return feedbackRepository.findByStaffStaffid(staffId);
    }

    @Override
    public List<Feedback> findByCustomerId(int customerId) {
        return feedbackRepository.findByCustomerId(customerId);
    }

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
}
