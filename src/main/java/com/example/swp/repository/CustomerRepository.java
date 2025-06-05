package com.example.swp.repository;

import com.example.swp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> { // Giả sử ID là Long
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String email); // Cần thiết để kiểm tra email tồn tại
}