package com.example.swp.controller.website;

import com.example.swp.entity.Order;
import com.example.swp.entity.StorageTransaction;
import com.example.swp.service.EmailService;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/SWP")
public class StorageTransactionDetailController {

    @Autowired
    private StorageTransactionService storageTransactionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/storage-transactions/{id}")
    public String viewStorageTransactionDetail(@PathVariable int id, Model model) {
        Optional<StorageTransaction> optionalTransaction = storageTransactionService.getStorageTransactionById(id);
        if (optionalTransaction.isPresent()) {
            model.addAttribute("transaction", optionalTransaction.get());
            return "staff-transaction-detail"; //
        } else {
            return "redirect:/SWP/storage-transactions";
        }
    }


    @PostMapping("/storage-transactions/{id}/mark-import")
    public String markStorageTransactionAsImport(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> optionalTransaction = storageTransactionService.getStorageTransactionById(id);
        if (optionalTransaction.isPresent()) {
            StorageTransaction transaction = optionalTransaction.get();
            if (!"PENDING".equals(transaction.getType())) {
                redirectAttributes.addFlashAttribute("error", "Giao dịch không ở trạng thái PENDING.");
                return "redirect:/SWP/storage-transactions/" + id;
            }
            transaction.setType("IMPORT");
            storageTransactionService.save(transaction);

            String customerEmail = transaction.getCustomer().getEmail();
            String storageInfo = transaction.getStorage().getStoragename();
            String subject = "Xác nhận giao dịch kho #" + id + " - Nhập kho";
            String body = "Kính gửi khách hàng,\n\n" +
                    "Giao dịch kho #" + id + " của bạn đã được đánh dấu là Nhập kho.\n" +
                    "Kho: " + storageInfo + "\n" +
                    "Loại giao dịch: " + transaction.getType() + "\n\n" +
                    "Cảm ơn bạn đã sử dụng dịch vụ!";

            emailService.sendEmail(customerEmail, subject, body);

            redirectAttributes.addFlashAttribute("message", "Đã đánh dấu giao dịch kho #" + id + " là IMPORT.");
            return "redirect:/SWP/storage-transactions/" + id;
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch kho.");
            return "redirect:/SWP/storage-transactions";
        }
    }

    @PostMapping("/storage-transactions/{id}/mark-export")
    public String markStorageTransactionAsExport(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> optionalTransaction = storageTransactionService.getStorageTransactionById(id);
        if (optionalTransaction.isPresent()) {
            StorageTransaction transaction = optionalTransaction.get();
            if (!"IMPORT".equals(transaction.getType())) {
                redirectAttributes.addFlashAttribute("error", "Giao dịch phải ở trạng thái IMPORT trước khi EXPORT.");
                return "redirect:/SWP/storage-transactions/" + id;
            }
            transaction.setType("EXPORT");
            storageTransactionService.save(transaction);

            // Cập nhật trạng thái Order thành COMPLETED
            Optional<Order> optionalOrder = orderService.findOrderByCustomerAndStorage(
                    transaction.getCustomer().getId(), transaction.getStorage().getStorageid());
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                order.setStatus("COMPLETED");
                orderService.save(order);
            } else {
                redirectAttributes.addFlashAttribute("warning", "Không tìm thấy đơn hàng liên quan để cập nhật trạng thái.");
            }

            String customerEmail = transaction.getCustomer().getEmail();
            String storageInfo = transaction.getStorage().getStoragename();
            String subject = "Xác nhận giao dịch kho #" + id + " - Xuất kho";
            String body = "Kính gửi khách hàng,\n\n" +
                    "Giao dịch kho #" + id + " của bạn đã được đánh dấu là Xuất kho.\n" +
                    "Kho: " + storageInfo + "\n" +
                    "Loại giao dịch: " + transaction.getType() + "\n" +
                    "Đơn hàng của bạn đã hoàn thành.\n\n" +
                    "Cảm ơn bạn đã sử dụng dịch vụ!";

            emailService.sendEmail(customerEmail, subject, body);

            redirectAttributes.addFlashAttribute("message", "Đã đánh dấu giao dịch kho #" + id + " là EXPORT và cập nhật đơn hàng.");
            return "redirect:/SWP/storage-transactions/" + id;
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch kho.");
            return "redirect:/SWP/storage-transactions";
        }
    }
}