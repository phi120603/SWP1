package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Feedback;
import com.example.swp.entity.Storage;
import com.example.swp.repository.FeedbackRepository;
import com.example.swp.repository.OrderRepository;
import com.example.swp.service.FeedbackService;
import com.example.swp.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/SWP") // Thêm base path
public class FeedbackController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/create/{storageId}")
    public String showFeedbackForm(@PathVariable int storageId,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đánh giá.");
            return "redirect:/api/login";
        }

        // Kiểm tra kho tồn tại
        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho.");
            return "redirect:/SWP/storages";
        }
        Storage storage = optionalStorage.get();

        // Kiểm tra quyền đánh giá (phải có order được approved)
        boolean hasApprovedOrder = orderRepository.existsByCustomerAndStorageAndStatus(customer, storage, "APPROVED");
        if (!hasApprovedOrder) {
            redirectAttributes.addFlashAttribute("error", "Bạn chỉ được đánh giá khi đã thuê và được duyệt kho này.");
            return "redirect:/SWP/storages/" + storageId;
        }

        // Tìm feedback cũ hoặc tạo mới
        Feedback feedback = feedbackService.findByCustomerAndStorage(customer, storage)
                .orElseGet(() -> {
                    Feedback fb = new Feedback();
                    fb.setCustomer(customer);
                    fb.setStorage(storage);
                    return fb;
                });

        model.addAttribute("feedback", feedback);
        model.addAttribute("storage", storage);
        return "feedback";
    }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback,
                                 @RequestParam int storageId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra đăng nhập
            Customer customer = (Customer) session.getAttribute("loggedInCustomer");
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đánh giá.");
                return "redirect:/api/login";
            }

            // Validation đầu vào
            String validationError = validateFeedback(feedback);
            if (validationError != null) {
                redirectAttributes.addFlashAttribute("error", validationError);
                return "redirect:/SWP/create/" + storageId;
            }

            // Xử lý tạo/cập nhật feedback
            feedbackService.createOrUpdateFeedback(storageId, customer.getId(),
                    feedback.getContent().trim(), feedback.getRating());

            // Thông báo thành công
            String successMessage = (feedback.getId() > 0) ?
                    "Cập nhật đánh giá thành công!" : "Gửi đánh giá thành công!";
            redirectAttributes.addFlashAttribute("message", successMessage);

            return "redirect:/SWP/storages/" + storageId;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/SWP/create/" + storageId;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/SWP/create/" + storageId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
            return "redirect:/SWP/create/" + storageId;
        }
    }

    /**
     * Validate feedback input
     */
    private String validateFeedback(Feedback feedback) {
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            return "Đánh giá phải từ 1 đến 5 sao.";
        }

        if (feedback.getContent() == null || feedback.getContent().trim().isEmpty()) {
            return "Vui lòng nhập nội dung đánh giá.";
        }

        if (feedback.getContent().trim().length() < 10) {
            return "Nội dung đánh giá phải có ít nhất 10 ký tự.";
        }

        if (feedback.getContent().trim().length() > 500) {
            return "Nội dung đánh giá không được vượt quá 500 ký tự.";
        }

        return null; // No validation errors
    }
}