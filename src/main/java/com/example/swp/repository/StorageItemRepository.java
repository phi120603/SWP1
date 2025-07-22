package com.example.swp.repository;

import com.example.swp.entity.Order;
import com.example.swp.entity.StorageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageItemRepository extends JpaRepository<StorageItem, Integer> {
    List<StorageItem> findByOrder(Order order);
}
