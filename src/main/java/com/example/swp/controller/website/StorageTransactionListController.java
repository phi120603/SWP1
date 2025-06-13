package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;
import com.example.swp.entity.StorageTransaction;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/SWP")
public class StorageTransactionListController {

    @Autowired
    private StorageTransactionService storageTransactionService;

    @GetMapping("/storage-transactions")
    public String listStorageTransactions(Model model) {
        List<StorageTransaction> transactions = storageTransactionService.getAllStorageTransactions();
        model.addAttribute("transactions", transactions);
        return "storage-transaction-list";
    }
    @PostMapping("/createStorageTransaction")
    public String createStorageTransaction(
            @RequestParam String type,
            @RequestParam String transactionDate,
            @RequestParam Integer storageId,
            @RequestParam Integer customerId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            StorageTransaction transaction = new StorageTransaction();
            transaction.setType(type);
            transaction.setTransactionDate(LocalDateTime.parse(transactionDate));
            transaction.setStorage(new Storage(storageId));   // hoặc gọi từ service nếu bạn có
            transaction.setCustomer(new Customer(customerId)); // tương tự

            storageTransactionService.save(transaction);

            redirectAttributes.addFlashAttribute("message", "Tạo giao dịch thành công.");
            return "redirect:/SWP/storage-transactions";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo giao dịch: " + e.getMessage());
            return "redirect:/SWP/storage-transactions";
        }
    }

}