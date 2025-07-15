package com.example.swp.service;

import com.example.swp.dto.StorageTransactionRequest;
import com.example.swp.entity.StorageTransaction;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Optional;

public interface StorageTransactionService {
    List<StorageTransaction> getAllStorageTransactions();

    Optional<StorageTransaction> getStorageTransactionById(int id);

    List<StorageTransaction> findByCustomerId(int customerId);

    StorageTransaction save(StorageTransaction transaction);

    StorageTransaction createStorageTransaction(StorageTransactionRequest transactionRequest);
    StorageTransaction findById(Integer id);


}
