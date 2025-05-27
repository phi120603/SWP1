package com.example.swp.repository;

import com.example.swp.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageResponsitory extends JpaRepository<Storage, Integer> {
}
