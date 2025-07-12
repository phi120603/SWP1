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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    public List<StorageTransaction> findByCustomerId(int customerId) {
        return storageTransactionRepository.findByCustomerId(customerId);
    }

    @Override
    public StorageTransaction save(StorageTransaction transaction) {
        return storageTransactionRepository.save(transaction);
    }

    @Override
    public StorageTransaction createStorageTransaction(StorageTransactionRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));
        Storage storage = storageRepository.findById(request.getStorageId())
                .orElseThrow(() -> new RuntimeException("Storage not found with id: " + request.getStorageId()));

        StorageTransaction transaction = new StorageTransaction();
        transaction.setType(request.getType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCustomer(customer);
        transaction.setStorage(storage);
        transaction.setAmount(request.getAmount());

        return storageTransactionRepository.save(transaction);
    }
}
