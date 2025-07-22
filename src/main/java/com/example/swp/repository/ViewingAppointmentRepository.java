package com.example.swp.repository;

import com.example.swp.entity.ViewingAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ViewingAppointmentRepository extends JpaRepository<ViewingAppointment, Integer> {

    @Query("SELECT v FROM ViewingAppointment v WHERE "
            + "(:status IS NULL OR v.status = :status) "
            + "AND (:warehouse IS NULL OR v.storage.storageid = :warehouse) "
            + "AND (:fromDate IS NULL OR v.appointmentDateTime >= :fromDate) "
            + "AND (:toDate IS NULL OR v.appointmentDateTime <= :toDate)")
    Page<ViewingAppointment> findByFilters(@Param("status") String status,
                                           @Param("warehouse") String warehouse,
                                           @Param("fromDate") LocalDateTime fromDate,
                                           @Param("toDate") LocalDateTime toDate,
                                           Pageable pageable);
}
