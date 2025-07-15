package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.service.FeedbackService;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
@RequestMapping("/SWP")
public class StorageDetailController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/storages/{id}")
    public String viewStorageDetail(@PathVariable("id") int storageId,
                                    Model model,
                                    HttpSession session) {

        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            return "redirect:/SWP/storages";
        }

        Storage storage = optionalStorage.get();
        model.addAttribute("storage", storage);

        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        boolean canFeedback = false;
        boolean hasFeedbacked = false;

        if (customer != null) {
            int customerId = customer.getId();
            canFeedback = orderService.canCustomerFeedback(customerId, storageId);
            hasFeedbacked = feedbackService.hasCustomerFeedbacked(customerId, storageId);
        }

        model.addAttribute("canFeedback", canFeedback);
        model.addAttribute("hasFeedbacked", hasFeedbacked);
        model.addAttribute("feedbacks", feedbackService.findByStorageId(storageId));

        return "storage-detail";
    }
}
