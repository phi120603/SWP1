package com.example.swp.service.impl;

import com.example.swp.entity.StorageItem;
import com.example.swp.repository.StorageItemRepository;
import com.example.swp.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.swp.service.StorageItemService;
import java.util.List;

@Service
public class StorageItemServiceImpl implements StorageItemService {
    @Autowired
    private StorageItemRepository storageItemRepository;

    @Override
    public List<StorageItem> getStorageItemsByOrder(Order order) {
        return storageItemRepository.findByOrder(order);
    }

    @Override
    public StorageItem saveStorageItem(StorageItem storageItem) {
        return storageItemRepository.save(storageItem);
    }

    @Override
    public StorageItem getStorageItemById(int id) {
        return storageItemRepository.findById(id).orElse(null);
    }


    @Override
    public void deleteStorageItem(int id) {
        storageItemRepository.deleteById(id);
    }
}

