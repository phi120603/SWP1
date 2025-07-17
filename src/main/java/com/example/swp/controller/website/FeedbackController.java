package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.service.FeedbackService;
import com.example.swp.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/SWP/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private OrderService orderService;

    /**
     * Gửi feedback nếu khách đã thanh toán đơn hàng & chưa đánh giá trước đó.
     */
    @PostMapping("/submit")
    public String submitFeedback(@RequestParam("storageId") int storageId,
                                 @RequestParam("content") String content,
                                 @RequestParam("rating") int rating,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        // Lấy customer từ session
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        if (customer == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để gửi đánh giá.");
            return "redirect:/login";
        }

        int customerId = customer.getId(); // dùng getId() thay vì getCustomerid()

        // Kiểm tra đã thanh toán đơn hay chưa
        boolean canFeedback = orderService.canCustomerFeedback(customerId, storageId);
        if (!canFeedback) {
            redirectAttributes.addFlashAttribute("error", "Bạn chỉ có thể đánh giá sau khi đã thanh toán.");
            return "redirect:/SWP/storages/" + storageId;
        }

        // Kiểm tra đã feedback trước đó chưa
        boolean alreadyFeedbacked = feedbackService.hasCustomerFeedbacked(customerId, storageId);
        if (alreadyFeedbacked) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã gửi đánh giá cho kho này rồi.");
            return "redirect:/SWP/storages/" + storageId;
        }

        // Lưu feedback
        feedbackService.createFeedback(storageId, customerId, content, rating);
        redirectAttributes.addFlashAttribute("message", "Cảm ơn bạn đã gửi đánh giá!");
        return "redirect:/SWP/storages/" + storageId;
    }
}