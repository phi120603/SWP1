package com.example.swp.repository;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    // Thêm hàm này để tìm theo tên (search)
    List<Customer> findByFullnameContainingIgnoreCase(String fullname);

    List<Customer> findByRoleName(RoleName roleName);

}
