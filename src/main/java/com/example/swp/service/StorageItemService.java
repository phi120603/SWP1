package com.example.swp.service;

import com.example.swp.entity.Order;
import com.example.swp.entity.StorageItem;


import java.util.List;

public interface StorageItemService {
    List<StorageItem> getStorageItemsByOrder(Order order);
    StorageItem saveStorageItem(StorageItem storageItem);
    void deleteStorageItem(int id);
    StorageItem getStorageItemById(int id);
}
