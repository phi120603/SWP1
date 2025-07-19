package com.example.swp.repository;

import com.example.swp.entity.ActivityLog;
import com.example.swp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByCustomerOrderByTimestampDesc(Customer customer);
}
