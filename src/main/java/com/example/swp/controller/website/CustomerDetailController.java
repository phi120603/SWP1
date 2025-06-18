package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Feedback;
import com.example.swp.service.CustomerService;
import com.example.swp.service.OrderService;
import com.example.swp.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/SWP/customers")
public class CustomerDetailController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/{id}")
    public String customerDetail(@PathVariable int id, Model model) {
        Customer customer = customerService.getCustomer(id);
        if (customer == null) {
            return "redirect:/SWP/customers?notfound=true";
        }
        List<Order> orders = orderService.findOrdersByCustomer(customer);
        List<Feedback> feedbacks = feedbackRepository.findByCustomer(customer);

        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        model.addAttribute("feedbacks", feedbacks);
        return "customer-detail";
    }
}
