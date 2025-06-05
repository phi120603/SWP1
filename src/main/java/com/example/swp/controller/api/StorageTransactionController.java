package com.example.swp.controller.api;

import com.example.swp.dto.StorageTransactionRequest;
import com.example.swp.entity.StorageTransaction;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/storage-transaction")
public class StorageTransactionController {

    @Autowired
    private StorageTransactionService storageTransactionService;

    @GetMapping("/all-transactions")
    public List<StorageTransaction> getAllStorageTransactions() {
        return storageTransactionService.getAllStorageTransactions();
    }

    @GetMapping("/findByID/{id}")
    public Optional<StorageTransaction> findById(@PathVariable int id) {
        return storageTransactionService.getStorageTransactionById(id);
    }

    @PostMapping("/createStorageTransaction")
    public String createStorageTransaction(
            @RequestParam String type,
            @RequestParam String transactionDate,
            @RequestParam Integer storageId,
            @RequestParam Integer customerId,
            Model model) {

        try {
            StorageTransactionRequest request = new StorageTransactionRequest();
            request.setType(type);
            request.setTransactionDate(LocalDateTime.parse(transactionDate));
            request.setStorageId(storageId);
            request.setCustomerId(customerId);

            storageTransactionService.createStorageTransaction(request);
            return "redirect:/SWP/storage-transactions";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating transaction: " + e.getMessage());
            List<StorageTransaction> transactions = storageTransactionService.getAllStorageTransactions();
            model.addAttribute("transactions", transactions);
            return "storage-transaction-list";
        }
}
}