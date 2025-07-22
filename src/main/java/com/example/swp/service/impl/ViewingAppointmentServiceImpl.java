package com.example.swp.service.impl;

import com.example.swp.entity.ViewingAppointment;
import com.example.swp.repository.ViewingAppointmentRepository;
import com.example.swp.service.ViewingAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ViewingAppointmentServiceImpl implements ViewingAppointmentService {

    @Autowired
    private ViewingAppointmentRepository repository;

    @Override
    public void save(ViewingAppointment appointment) {
        repository.save(appointment);
    }

    @Override
    public List<ViewingAppointment> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ViewingAppointment> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public Page<ViewingAppointment> findFilteredPaginated(String status, String warehouse,
                                                          String fromDateStr, String toDateStr,
                                                          int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDateTime").descending());

        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (fromDateStr != null && !fromDateStr.isEmpty()) {
            fromDate = LocalDate.parse(fromDateStr, formatter).atStartOfDay();
        }

        if (toDateStr != null && !toDateStr.isEmpty()) {
            toDate = LocalDate.parse(toDateStr, formatter).atTime(23, 59, 59);
        }

        return repository.findByFilters(status, warehouse, fromDate, toDate, pageable);
    }
}
