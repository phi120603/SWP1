package com.example.swp.service;

import com.example.swp.entity.ViewingAppointment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ViewingAppointmentService {
    void save(ViewingAppointment appointment);
    List<ViewingAppointment> findAll();
    Optional<ViewingAppointment> findById(int id);

    Page<ViewingAppointment> findFilteredPaginated(String status, String warehouse,
                                                   String fromDate, String toDate,
                                                   int page, int size);
}
