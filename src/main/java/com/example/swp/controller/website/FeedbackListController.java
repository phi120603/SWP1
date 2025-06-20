package com.example.swp.controller.website;

import com.example.swp.entity.Feedback;
import com.example.swp.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/SWP")
public class FeedbackListController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedbacks")
    public String listFeedbacks(
            @RequestParam(value = "staffId", required = false) Integer staffId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            Model model) {
        List<Feedback> feedbacks;
        if (staffId != null) {
            feedbacks = feedbackService.findByStaffId(staffId);
        } else if (customerId != null) {
            feedbacks = feedbackService.findByCustomerId(customerId);
        } else {
            feedbacks = feedbackService.getAllFeedbacks();
        }
        model.addAttribute("feedbacks", feedbacks);
        return "feedback-list";
    }
}
