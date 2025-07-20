package com.example.swp.repository;

import com.example.swp.entity.EContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EContractRepository extends JpaRepository<EContract, Integer> {
}
