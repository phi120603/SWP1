package com.example.swp.service;

import com.example.swp.dto.StorageTransactionRequest;
import com.example.swp.entity.StorageTransaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StorageTransactionService {
    List<StorageTransaction> getAllStorageTransactions();

    Optional<StorageTransaction> getStorageTransactionById(int id);

    StorageTransaction createStorageTransaction(StorageTransactionRequest transactionRequest);

    StorageTransaction save(StorageTransaction transaction);
}