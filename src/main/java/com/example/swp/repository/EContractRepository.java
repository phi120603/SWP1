package com.example.swp.repository;

import com.example.swp.entity.EContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EContractRepository extends JpaRepository<EContract, Integer> {
    List<EContract> findByCustomerId(Integer customerId);
}
