package com.example.swp.repository;

import com.example.swp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Customer, Integer> {
}
