package com.example.swp.repository;

import com.example.swp.entity.SupportActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportActivityRepository extends JpaRepository<SupportActivity, Integer> {
    List<SupportActivity> findTop10ByOrderByActivityTimeDesc();
}
