package com.example.swp.controller.website;

import com.example.swp.entity.StorageTransaction;
import com.example.swp.enums.TransactionType;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/SWP/staff/transactions")
public class StorageTransactionListController {

    @Autowired
    private StorageTransactionService storageTransactionService;

    // 1. Lấy danh sách transaction cho staff (có filter type nếu muốn)
    @GetMapping
    public String listTransactions(
            @RequestParam(value = "type", required = false) TransactionType type,
            @RequestParam(value = "customer", required = false) String customerName,
            @RequestParam(value = "storage", required = false) String storageName,
            Model model
    ) {
        List<StorageTransaction> transactions = storageTransactionService.getFilteredTransactions(type, customerName, storageName);
        model.addAttribute("transactions", transactions);
        return "staff-transaction-list";
    }


    // 2. Xem chi tiết 1 transaction
    @GetMapping("/{id}")
    public String transactionDetail(@PathVariable int id, Model model) {
        StorageTransaction transaction = storageTransactionService.getStorageTransactionById(id)
                .orElse(null);
        if (transaction == null) {
            // Có thể redirect hoặc trả về trang thông báo lỗi
            model.addAttribute("error", "Không tìm thấy giao dịch.");
            return "staff-transaction-detail";
        }
        model.addAttribute("transaction", transaction);
        return "staff-transaction-detail";
    }
}
