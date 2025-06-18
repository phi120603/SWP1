package com.example.swp.service.impl;

import com.example.swp.entity.Feedback;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.FeedbackRepository;
import com.example.swp.repository.OrderRepository;
import com.example.swp.repository.StorageRepository;
import com.example.swp.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private StorageRepository storageRepo;

    @Override
    public Feedback createOrUpdateFeedback(int storageId, int customerId, String content, int rating) {
        boolean hasApprovedOrder = orderRepo.existsByCustomerIdAndStorageStorageidAndStatus(customerId, storageId, "APPROVED");
        if (!hasApprovedOrder) {
            throw new IllegalArgumentException("Bạn phải thuê kho này và đã được duyệt mới được feedback.");
        }
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Storage storage = storageRepo.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found"));

        Feedback feedback = feedbackRepo.findByCustomerAndStorage(customer, storage)
                .orElseGet(Feedback::new);

        feedback.setCustomer(customer);
        feedback.setStorage(storage);
        feedback.setContent(content);
        feedback.setRating(rating);

        return feedbackRepo.save(feedback);
    }

    @Override
    public Feedback createFeedback(int storageId, int customerId, String content, int rating) {
        boolean hasApprovedOrder = orderRepo.existsByCustomerIdAndStorageStorageidAndStatus(customerId, storageId, "APPROVED");
        if (!hasApprovedOrder) {
            throw new IllegalArgumentException("Bạn phải thuê kho này và đã được duyệt mới được feedback.");
        }
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Storage storage = storageRepo.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found"));
        if (feedbackRepo.existsByCustomerAndStorage(customer, storage)) {
            throw new IllegalStateException("Bạn đã feedback kho này rồi.");
        }
        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setStorage(storage);
        feedback.setContent(content);
        feedback.setRating(rating);

        return feedbackRepo.save(feedback);
    }

    @Override
    public Optional<Feedback> findByCustomerAndStorage(Customer customer, Storage storage) {
        return feedbackRepo.findByCustomerAndStorage(customer, storage);
    }

    @Override
    public boolean existsByCustomerAndStorage(Customer customer, Storage storage) {
        return feedbackRepo.existsByCustomerAndStorage(customer, storage);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepo.findAll();
    }

    @Override
    public Feedback getFeedbackById(int id) {
        return feedbackRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteFeedback(int id) {
        feedbackRepo.deleteById(id);
    }
}
