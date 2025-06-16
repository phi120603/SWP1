package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Feedback;
import com.example.swp.service.CustomerService;
import com.example.swp.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listFeedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "feedback-list";
    }

    @GetMapping("/create")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback-form";
    }

    @PostMapping("/create")
    public String submitFeedback(@ModelAttribute("feedback") Feedback feedback,
                                 @RequestParam("customerId") int customerId) {
        Customer customer = customerService.getCustomer(customerId);
        feedback.setCustomer(customer);

        feedbackService.createFeedback(feedback);
        return "redirect:/feedbacks";
    }

    @PostMapping("/{id}/delete")
    public String deleteFeedback(@PathVariable("id") int id) {
        feedbackService.deleteFeedback(id);
        return "redirect:/feedbacks";
    }
}
