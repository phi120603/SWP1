package com.example.swp.repository;

import com.example.swp.entity.Storage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {
//    @Modifying
//    @Transactional
//    @Query(value = "ALTER TABLE storage AUTO_INCREMENT = 1", nativeQuery = true)
//    void resetAutoIncrement();
@Query("""
    SELECT s FROM Storage s
    WHERE s.status = true
      AND (:minArea IS NULL OR s.area >= :minArea)
      AND NOT EXISTS (
          SELECT o FROM Order o
          WHERE o.storage = s
            AND o.status IN ('PENDING', 'CONFIRMED', 'ACTIVE')
            AND o.startDate <= :endDate
            AND o.endDate >= :startDate
      )
""")
List<Storage> findAvailableStorages(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minArea") Double minArea);

    Optional<Storage> findById(int id);



}
