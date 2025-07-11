package com.example.swp.service;

import com.example.swp.entity.ViewingAppointment;

import java.util.List;
import java.util.Optional;

public interface ViewingAppointmentService {
    void save(ViewingAppointment appointment);
    List<ViewingAppointment> findAll();
    Optional<ViewingAppointment> findById(int id);
}
