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
    long countByStatus(boolean status);
//    @Modifying
//    @Transactional
//    @Query(value = "ALTER TABLE storage AUTO_INCREMENT = 1", nativeQuery = true)
//    void resetAutoIncrement();
@Query("""
    SELECT s FROM Storage s
    WHERE s.status = true
      AND (:minArea IS NULL OR s.area >= :minArea)
      AND (:minPrice IS NULL OR s.pricePerDay >= :minPrice)
      AND (:maxPrice IS NULL OR s.pricePerDay <= :maxPrice)
      AND (:city IS NULL OR :city = '' OR s.city = :city)
      AND (:nameKeyword IS NULL OR
           LOWER(s.storagename) LIKE LOWER(CONCAT('%', :nameKeyword, '%'))
        OR LOWER(s.address) LIKE LOWER(CONCAT('%', :nameKeyword, '%')))
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
        @Param("minArea") Double minArea,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("nameKeyword") String nameKeyword,
        @Param("city") String city // vẫn giữ param này!
);



    Optional<Storage> findById(int id);
    @Query("SELECT DISTINCT s.city FROM Storage s WHERE s.status = true ORDER BY s.city ASC")
    List<String> findAllCities();




}
