package com.example.swp.repository;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    boolean existsByCustomerAndStorageAndStatus(Customer customer, Storage storage, String status);
    boolean existsByCustomerIdAndStorageStorageidAndStatus(int customerId, int storageid, String status);



}
