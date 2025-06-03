
package com.example.swp.service;

import com.example.swp.entity.Customer;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public interface LoginService {
    Optional<Customer> findByEmail(String email);
}

