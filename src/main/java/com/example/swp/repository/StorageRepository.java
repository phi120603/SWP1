package com.example.swp.repository;

import com.example.swp.entity.Storage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {
    long countByStatus(boolean status);
//    @Modifying
//    @Transactional
//    @Query(value = "ALTER TABLE storage AUTO_INCREMENT = 1", nativeQuery = true)
//    void resetAutoIncrement();
}
