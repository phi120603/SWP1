package com.example.swp.repository;

import com.example.swp.entity.StorageTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageTransactionRepository extends JpaRepository<StorageTransaction, Integer> {
}