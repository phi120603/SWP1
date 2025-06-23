package com.example.swp.service.impl;

import com.example.swp.dto.StorageTransactionRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;
import com.example.swp.entity.StorageTransaction;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.StorageRepository;
import com.example.swp.repository.StorageTransactionRepository;
import com.example.swp.service.StorageTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StorageTransactionServiceImpl implements StorageTransactionService {

    @Autowired
    private StorageTransactionRepository storageTransactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Override
    public List<StorageTransaction> getAllStorageTransactions() {
        return storageTransactionRepository.findAll();
    }

    @Override
    public Optional<StorageTransaction> getStorageTransactionById(int id) {
        return storageTransactionRepository.findById(id);
    }

    @Override
    public StorageTransaction createStorageTransaction(StorageTransactionRequest transactionRequest) {
        Customer customer = customerRepository.findById(transactionRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + transactionRequest.getCustomerId()));
        Storage storage = storageRepository.findById(transactionRequest.getStorageId())
                .orElseThrow(() -> new RuntimeException("Storage not found with id: " + transactionRequest.getStorageId()));

        StorageTransaction transaction = new StorageTransaction();
        transaction.setType(transactionRequest.getType());
        transaction.setTransactionDate(transactionRequest.getTransactionDate());
        transaction.setCustomer(customer);
        transaction.setStorage(storage);

        return storageTransactionRepository.save(transaction);
    }

    @Override
    public StorageTransaction save(StorageTransaction transaction) {
        return storageTransactionRepository.save(transaction);
    }
}