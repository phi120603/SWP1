package com.example.swp.controller.website;

import com.example.swp.entity.StorageTransaction;
import com.example.swp.enums.TransactionType;
import com.example.swp.service.EmailService;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/SWP/storage-transactions")
public class StorageTransactionDetailController {

    @Autowired
    private StorageTransactionService storageTransactionService;

    @Autowired
    private EmailService emailService;

    // Hiển thị chi tiết giao dịch
    @GetMapping("/{id}")
    public String viewTransactionDetail(@PathVariable int id, Model model) {
        Optional<StorageTransaction> opt = storageTransactionService.getStorageTransactionById(id);
        if (opt.isEmpty()) {
            return "redirect:/SWP/storage-transactions";
        }

        model.addAttribute("transaction", opt.get());
        return "staff-transaction-detail";
    }

    // Đánh dấu hoàn tiền (chuyển từ PAID -> REFUND)
    @PostMapping("/{id}/mark-refund")
    public String markAsRefund(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> opt = storageTransactionService.getStorageTransactionById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch.");
            return "redirect:/SWP/storage-transactions";
        }

        StorageTransaction transaction = opt.get();
        if (transaction.getType() != TransactionType.PAID) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể hoàn tiền cho giao dịch PAID.");
            return "redirect:/SWP/storage-transactions/" + id;
        }

        transaction.setType(TransactionType.REFUND);
        storageTransactionService.save(transaction);

        // Gửi email
        String email = transaction.getCustomer().getEmail();
        String subject = "Xác nhận hoàn tiền giao dịch #" + id;
        String body = "Kính gửi khách hàng,\n\n"
                + "Chúng tôi xác nhận đã hoàn tiền cho giao dịch #" + id + " liên quan đến kho "
                + transaction.getStorage().getStoragename() + ".\n\n"
                + "Trân trọng,\nQVL Storage";

        emailService.sendEmail(email, subject, body);

        redirectAttributes.addFlashAttribute("message", "Giao dịch #" + id + " đã được hoàn tiền.");
        return "redirect:/SWP/storage-transactions/" + id;
    }

    // (Tuỳ chọn) Đánh dấu lại là đã thanh toán (REFUND -> PAID)
    @PostMapping("/{id}/mark-paid")
    public String markAsPaid(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> opt = storageTransactionService.getStorageTransactionById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch.");
            return "redirect:/SWP/storage-transactions";
        }

        StorageTransaction transaction = opt.get();
        if (transaction.getType() != TransactionType.REFUND) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể chuyển từ REFUND về PAID nếu cần.");
            return "redirect:/SWP/storage-transactions/" + id;
        }

        transaction.setType(TransactionType.PAID);
        storageTransactionService.save(transaction);

        redirectAttributes.addFlashAttribute("message", "Giao dịch #" + id + " đã được đánh dấu là PAID lại.");
        return "redirect:/SWP/storage-transactions/" + id;
    }
    @PostMapping("/storage-transactions/{id}/approve-refund")
    public String approveRefund(@PathVariable int id, RedirectAttributes redirect) {
        var transaction = storageTransactionService.getStorageTransactionById(id);
        if (transaction.isEmpty() || transaction.get().getType() != TransactionType.REQUESTED) {
            redirect.addFlashAttribute("error", "Giao dịch không hợp lệ hoặc không ở trạng thái REQUESTED.");
            return "redirect:/SWP/storage-transactions";
        }
        StorageTransaction tx = transaction.get();
        tx.setType(TransactionType.REFUND);
        storageTransactionService.save(tx);

        // Gửi email
        emailService.sendEmail(
                tx.getCustomer().getEmail(),
                "Hoàn tiền được chấp nhận",
                "Yêu cầu hoàn tiền của bạn đã được chấp thuận cho giao dịch #" + id + "."
        );
        redirect.addFlashAttribute("message", "Đã duyệt hoàn tiền giao dịch #" + id);
        return "redirect:/SWP/storage-transactions/" + id;
    }

    @PostMapping("/storage-transactions/{id}/deny-refund")
    public String denyRefund(@PathVariable int id, RedirectAttributes redirect) {
        var transaction = storageTransactionService.getStorageTransactionById(id);
        if (transaction.isEmpty() || transaction.get().getType() != TransactionType.REQUESTED) {
            redirect.addFlashAttribute("error", "Giao dịch không hợp lệ hoặc không ở trạng thái REQUESTED.");
            return "redirect:/SWP/storage-transactions";
        }
        StorageTransaction tx = transaction.get();
        tx.setType(TransactionType.DENIED);
        storageTransactionService.save(tx);

        // Gửi email
        emailService.sendEmail(
                tx.getCustomer().getEmail(),
                "Hoàn tiền bị từ chối",
                "Yêu cầu hoàn tiền của bạn cho giao dịch #" + id + " đã bị từ chối."
        );
        redirect.addFlashAttribute("message", "Đã từ chối hoàn tiền giao dịch #" + id);
        return "redirect:/SWP/storage-transactions/" + id;
    }

}
