package com.example.swp.controller.website;

import com.example.swp.entity.StorageTransaction;
import com.example.swp.service.EmailService;
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
    private EmailService emailService;

    @GetMapping("/storage-transactions/{id}")
    public String viewStorageTransactionDetail(@PathVariable int id, Model model) {
        Optional<StorageTransaction> optionalTransaction = storageTransactionService.getStorageTransactionById(id);
        if (optionalTransaction.isPresent()) {
            model.addAttribute("transaction", optionalTransaction.get());
        } else {
            return "redirect:/SWP/storage-transactions";
        }
        return "storage-transaction-detail";
    }

    @PostMapping("/storage-transactions/{id}/complete")
    public String completeStorageTransaction(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> optionalTransaction = storageTransactionService.getStorageTransactionById(id);
        if (optionalTransaction.isPresent()) {
            StorageTransaction transaction = optionalTransaction.get();
            transaction.setType("COMPLETED");
            storageTransactionService.save(transaction);

            String customerEmail = transaction.getCustomer().getEmail();
            String storageInfo = transaction.getStorage().getStoragename();
            String subject = "Xác nhận giao dịch kho #" + id + " đã hoàn thành";
            String body = "Kính gửi khách hàng,\n\n" +
                    "Giao dịch kho #" + id + " của bạn đã hoàn thành thành công.\n" +
                    "Kho: " + storageInfo + "\n" +
                    "Loại giao dịch: " + transaction.getType() + "\n\n" +
                    "Cảm ơn bạn đã sử dụng dịch vụ!";

            emailService.sendEmail(customerEmail, subject, body);

            redirectAttributes.addFlashAttribute("message", "Đã hoàn thành giao dịch kho #" + id + " và gửi email xác nhận.");
            return "redirect:/SWP/storage-transactions/" + id;
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch kho.");
            return "redirect:/SWP/storage-transactions";
        }
    }

    @PostMapping("/storage-transactions/{id}/fail")
    public String failStorageTransaction(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<StorageTransaction> optionalTransaction = storageTransactionService.getStorageTransactionById(id);
        if (optionalTransaction.isPresent()) {
            StorageTransaction transaction = optionalTransaction.get();
            transaction.setType("FAILED");
            storageTransactionService.save(transaction);

            redirectAttributes.addFlashAttribute("message", "Đã đánh dấu giao dịch kho #" + id + " thất bại.");
            return "redirect:/SWP/storage-transactions/" + id;
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch kho.");
            return "redirect:/SWP/storage-transactions";
        }
    }
}