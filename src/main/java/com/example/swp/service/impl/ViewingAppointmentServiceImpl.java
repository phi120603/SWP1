package com.example.swp.service.impl;

import com.example.swp.entity.ViewingAppointment;
import com.example.swp.repository.ViewingAppointmentRepository;
import com.example.swp.service.ViewingAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
