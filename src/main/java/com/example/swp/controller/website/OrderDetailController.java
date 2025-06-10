package com.example.swp.controller.website;

import com.example.swp.entity.StorageTransaction;
import com.example.swp.repository.StorageTransactionRepository;
import com.example.swp.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.swp.entity.Order;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/SWP")
public class OrderDetailController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StorageTransactionRepository storageTransactionRepository;


    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable int id, Model model) {
        Optional<Order> optionalOrder = orderService    .getOrderById(id);
        if (optionalOrder.isPresent()) {
            model.addAttribute("order", optionalOrder.get());
        } else {
            // Xử lý nếu không tìm thấy đơn hàng
            return "redirect:/SWP/orders"; // quay lại danh sách
        }
        return "order-detail";
    }

    @PostMapping("/orders/{id}/approve")
    public String approveOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<Order> optionalOrder = orderService.getOrderById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // ✅ Cập nhật trạng thái đơn hàng
            order.setStatus("Approved");
            orderService.save(order);

            // ✅ Tạo StorageTransaction từ đơn hàng đã duyệt
            StorageTransaction transaction = new StorageTransaction();
            transaction.setType("RENT");
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setCustomer(order.getCustomer());
            transaction.setStorage(order.getStorage());
            storageTransactionRepository.save(transaction);


            // ✅ Gửi email xác nhận cho khách hàng
            String customerEmail = order.getCustomer().getEmail();
            String warehouseInfo = order.getStorage().getStoragename();
            double totalAmount = order.getTotalAmount();

            String subject = "Xác nhận đơn hàng #" + id + " đã được duyệt";
            String body = "Kính gửi khách hàng,\n\n" +
                    "Đơn hàng #" + id + " của bạn đã được duyệt thành công.\n" +
                    "Kho xuất hàng: " + warehouseInfo + "\n" +
                    "Tổng số tiền phải thanh toán: " + totalAmount + " VND\n\n" +
                    "Cảm ơn bạn đã mua hàng!";

            emailService.sendEmail(customerEmail, subject, body);

            redirectAttributes.addFlashAttribute("message", "Đã duyệt đơn hàng #" + id + " và tạo giao dịch kho.");
            return "redirect:/SWP/orders/{id}";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
            return "redirect:/SWP/orders";
        }
    }


    @PostMapping("/orders/{id}/reject")
    public String rejectOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<Order> optionalOrder = orderService.getOrderById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus("Rejected");
            orderService.save(order);

            redirectAttributes.addFlashAttribute("message", "Đã từ chối đơn hàng #" + id + " thành công.");
            return "redirect:/SWP/orders/{id}";  // redirect về trang chi tiết đơn hàng
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
            return "redirect:/SWP/orders";  // fallback: quay lại danh sách
        }
    }


    public StorageTransactionRepository getStorageTransactionRepository() {
        return storageTransactionRepository;
    }

    public void setStorageTransactionRepository(StorageTransactionRepository storageTransactionRepository) {
        this.storageTransactionRepository = storageTransactionRepository;
    }
}