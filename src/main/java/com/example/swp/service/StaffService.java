package com.example.swp.service;

import com.example.swp.dto.StaffRequest;
import com.example.swp.entity.Staff;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffService {
     List<Staff> getAllStaff();

     Staff createStaff(StaffRequest staffRequest);
}
