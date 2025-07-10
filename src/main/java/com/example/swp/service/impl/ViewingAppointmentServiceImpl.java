package com.example.swp.service.impl;

import com.example.swp.entity.ViewingAppointment;
import com.example.swp.repository.ViewingAppointmentRepository;
import com.example.swp.service.ViewingAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewingAppointmentServiceImpl implements ViewingAppointmentService {

    @Autowired
    private ViewingAppointmentRepository repository;

    @Override
    public void save(ViewingAppointment appointment) {
        repository.save(appointment);
    }
}
