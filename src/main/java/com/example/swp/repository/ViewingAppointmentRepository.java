package com.example.swp.repository;

import com.example.swp.entity.ViewingAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewingAppointmentRepository extends JpaRepository<ViewingAppointment, Integer> {
    List<ViewingAppointment> findAll();
}
