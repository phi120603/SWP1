package com.example.swp.service.impl;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Storage;
import com.example.swp.repository.StorageResponsitory;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StorageServiceimpl implements StorageService {
    @Autowired
    private StorageResponsitory storageResponsitory;
    @Override
    public List<Storage> getAll() {
        return storageResponsitory.findAll();
    }

    @Override
    public Storage createStorage(StorageRequest storageRequest) {
        Storage storage = new Storage();
        storage.setStoragename(storageRequest.getStoragename());
        storage.setAddress(storageRequest.getAddress());
        storage.setCity(storageRequest.getCity());
        storage.setState(storageRequest.getState());
        storage.setStatus(storageRequest.isStatus());
        return storageResponsitory.save(storage);
    }

    @Override
    public Optional<Storage> findByID(int id) {
        return storageResponsitory.findById(id);
    }

    @Override
    public Storage updateStorage(StorageRequest storageRequest, Storage storage) {

        storage.setStoragename(storageRequest.getStoragename());
        storage.setAddress(storageRequest.getAddress());
        storage.setCity(storageRequest.getCity());
        storage.setState(storageRequest.getState());
        storage.setStatus(storageRequest.isStatus());
        return storageResponsitory.save(storage);
    }


}
