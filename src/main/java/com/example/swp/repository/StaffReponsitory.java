package com.example.swp.repository;

import com.example.swp.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffReponsitory extends JpaRepository<Staff, Integer> {
}
