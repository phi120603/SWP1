package com.example.swp.repository;

import com.example.swp.entity.StorageTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageTransactionRepository extends JpaRepository<StorageTransaction, Integer> {
    List<StorageTransaction> findByCustomerId(Integer customerId);

}