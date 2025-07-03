package com.example.swp.service.impl;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Storage;
import com.example.swp.repository.StorageRepository;
import com.example.swp.repository.StorageRepository;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StorageServiceimpl implements StorageService {
    @Autowired
    private StorageRepository storageRepository;
    private List<Storage> storageList = new ArrayList<>();
    private Storage storage;
    @Override
    public List<Storage> getAll() {
        return storageRepository.findAll();
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

        return storageRepository.save(storage);
    }


    @Override
    public Optional<Storage> findByID(int id) {
        return storageRepository.findById(id);
    }

    @Override
    public Storage updateStorage(StorageRequest storageRequest, Storage storage) {

        storage.setStoragename(storageRequest.getStoragename());
        storage.setAddress(storageRequest.getAddress());
        storage.setCity(storageRequest.getCity());
        storage.setState(storageRequest.getState());
        storage.setStatus(storageRequest.isStatus());

        storage.setArea(storageRequest.getArea());
        storage.setPricePerDay(storageRequest.getPricePerDay());
        storage.setDescription(storageRequest.getDescription());

        if (storageRequest.getImUrl() != null && !storageRequest.getImUrl().isEmpty()) {
            storage.setImUrl(storageRequest.getImUrl());
        }

        return storageRepository.save(storage);
    }

    @Override
    public void save(Storage storage) {
        storageRepository.save(storage);
    }

    @Override
    public void deleteStorageById(int id) {
//        storageRepository.resetAutoIncrement();
        storageRepository.deleteById(id);


    }
    @Override
    public List<Storage> findAvailableStorages(
            LocalDate startDate, LocalDate endDate,
            Double minArea, Double minPrice,
            Double maxPrice, String nameKeyword, String city) {
        return storageRepository.findAvailableStorages(
                startDate, endDate, minArea, minPrice, maxPrice, nameKeyword, city);
    }

    @Override
    public List<String> findAllCities() {
        return storageRepository.findAllCities();
    }







}
