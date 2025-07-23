package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Feedback;
import com.example.swp.entity.StorageTransaction;
import com.example.swp.enums.TransactionType;
import com.example.swp.service.CustomerService;
import com.example.swp.service.OrderService;
import com.example.swp.repository.FeedbackRepository;
import com.example.swp.service.StorageTransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/SWP/customers")
public class CustomerDetailController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private StorageTransactionService storageTransactionService;



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
    @GetMapping("/my-bookings")
    public String viewMyBookings(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/api/login";
        }

        List<Order> orders = orderService.findOrdersByCustomer(customer);
        model.addAttribute("orders", orders);
        model.addAttribute("customer", customer);
        return "my-bookings"; // tạo file my-bookings.html
    }
    @PostMapping("/my-bookings/{id}/cancel")
    public String cancelBooking(
            @PathVariable("id") int orderId,
            @RequestParam("cancelReason") String cancelReason,
            @RequestParam(value = "otherReason", required = false) String otherReason,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/api/login";
        }

        Optional<Order> optionalOrder = orderService.getOrderById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (order.getCustomer().getId() == customer.getId() &&
                    ("PENDING".equals(order.getStatus()) || "CONFIRMED".equals(order.getStatus()))) {
                order.setStatus("CANCELLED");
                // Lưu đúng lý do
                if ("other".equals(cancelReason) && otherReason != null && !otherReason.isBlank()) {
                    order.setCancelReason(otherReason);
                } else {
                    order.setCancelReason(cancelReason);
                }
                orderService.save(order);
                redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn #" + orderId + " thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không thể hủy đơn này.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng.");
        }
        return "redirect:/SWP/customers/my-bookings";
    }



    @GetMapping("/my-transactions")
    public String viewMyTransactions(Model model, HttpSession session) {
        // Lấy customer đang đăng nhập từ session
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/login"; // hoặc trả về lỗi truy cập
        }

        // Lấy danh sách transaction theo customerId
        List<StorageTransaction> transactions = storageTransactionService.findByCustomerId(customer.getId());

        model.addAttribute("transactions", transactions);
        model.addAttribute("customer", customer); // nếu cần hiển thị tên ở giao diện

        return "my-transactions"; //
    }
    @PostMapping("/transactions/{id}/request-refund")

    public String requestRefund(@PathVariable int id,
                                @RequestParam("refundReason") String reason,
                                RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> optional = storageTransactionService.getStorageTransactionById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch.");
            return "redirect:/SWP/customers/my-transactions";
        }

        StorageTransaction transaction = optional.get();
        if (transaction.getType() != TransactionType.PAID) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể yêu cầu hoàn tiền cho giao dịch PAID.");
            return "redirect:/SWP/customers/my-transactions";
        }

        transaction.setType(TransactionType.REQUESTED);
        transaction.setRefundReason(reason);
        storageTransactionService.save(transaction);

        redirectAttributes.addFlashAttribute("message", "Yêu cầu hoàn tiền đã được gửi. Vui lòng chờ xác nhận.");
        return "redirect:/SWP/customers/my-transactions";
    }






}
