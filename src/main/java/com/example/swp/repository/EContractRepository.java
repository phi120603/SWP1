package com.example.swp.repository;

import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EContractRepository extends JpaRepository<EContract, Long> {
    List<EContract> findByStatus(EContractStatus status);
}
