package com.example.swp.controller.api;

import com.example.swp.dto.StorageTransactionRequest;
import com.example.swp.entity.StorageTransaction;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<StorageTransaction> createStorageTransaction(@RequestBody StorageTransactionRequest transactionRequest) {
        StorageTransaction transaction = storageTransactionService.createStorageTransaction(transactionRequest);
        return ResponseEntity.ok(transaction);
    }
}