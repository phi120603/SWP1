package com.example.swp.service.impl;

import com.example.swp.dto.StaffRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Staff;
import com.example.swp.repository.StaffReponsitory;
import com.example.swp.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StaffServiceimpl implements StaffService {
@Autowired
private StaffReponsitory staffReponsitory;
    @Override
    public List<Staff> getAllStaff() {
        return staffReponsitory.findAll() ;
    }

    @Override
    public Staff createStaff(StaffRequest staffRequest) {
        Staff staff = new Staff();
        staff.setFullname(staffRequest.getFullname());
        staff.setEmail(staffRequest.getEmail());
        staff.setPassword(staffRequest.getPassword());
        staff.setPhone(staffRequest.getPhone());
        staff.setRoleName(staffRequest.getRoleName());
        staff.setSex(staffRequest.isSex());
        staff.setIdCitizenCard(staffRequest.getIdCitizenCard());
        return staffReponsitory.save(staff);
    }

    @Override
    public Staff getStaff(int id) {
        return null;
    }


}
