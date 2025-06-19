package com.example.swp.service.impl;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Storage;
import com.example.swp.repository.StorageReponsitory;
import com.example.swp.repository.StorageReponsitory;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class StorageServiceimpl implements StorageService {
    @Autowired
    private StorageReponsitory storageReponsitory;
    @Override
    public List<Storage> getAll() {
        return storageReponsitory.findAll();
    }

    @Override
    public Storage createStorage(StorageRequest storageRequest) {
        Storage storage = new Storage();
        storage.setStoragename(storageRequest.getStoragename());
        storage.setAddress(storageRequest.getAddress());
        storage.setCity(storageRequest.getCity());
        storage.setState(storageRequest.getState());
        storage.setArea(storageRequest.getArea());
        storage.setPricePerDay(storageRequest.getPricePerDay());
        storage.setDescription(storageRequest.getDescription());
        storage.setStatus(storageRequest.isStatus());

        // Set ảnh nếu có
        if (storageRequest.getImUrl() != null && !storageRequest.getImUrl().isEmpty()) {
            storage.setImUrl(storageRequest.getImUrl());
        }

        return storageReponsitory.save(storage);
    }

    @Override
    public Optional<Storage> findByID(int id) {
        return storageReponsitory.findById(id);
    }

    @Override
    public Storage updateStorage(StorageRequest storageRequest, Storage storage) {
        // Cập nhật tất cả các trường có thể chỉnh sửa
        storage.setStoragename(storageRequest.getStoragename());
        storage.setAddress(storageRequest.getAddress());
        storage.setCity(storageRequest.getCity());
        storage.setState(storageRequest.getState());
        storage.setDescription(storageRequest.getDescription()); // Thêm dòng này
        storage.setStatus(storageRequest.isStatus());

        // Cập nhật các trường khác nếu có trong StorageRequest
        if (storageRequest.getArea() != -1) {
            storage.setArea(storageRequest.getArea());
        }
        if (storageRequest.getPricePerDay() != -1) {
            storage.setPricePerDay(storageRequest.getPricePerDay());
        }
        if (storageRequest.getImUrl() != null && !storageRequest.getImUrl().isEmpty()) {
            storage.setImUrl(storageRequest.getImUrl());
        }

        return storageReponsitory.save(storage);
    }

    @Override
    public void save(Storage storage) {
        storageReponsitory.save(storage);
    }

    @Override
    public void deleteStorageById(int id) {
        storageReponsitory.deleteById(id);
    }


}
