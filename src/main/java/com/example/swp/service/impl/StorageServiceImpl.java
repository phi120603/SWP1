package com.example.swp.service.impl;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Storage;
import com.example.swp.repository.StorageRepository;
import com.example.swp.service.GeocodingService;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Override
    public List<Storage> getAll() {
        return storageRepository.findAll().stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .collect(Collectors.toList());
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

        if (storageRequest.getImUrl() != null && !storageRequest.getImUrl().isEmpty()) {
            storage.setImUrl(storageRequest.getImUrl());
        }

        Optional<double[]> coordsOpt = geocodingService.geocode(storage.getAddress());
        if (coordsOpt.isPresent()) {
            double[] coords = coordsOpt.get();
            storage.setLatitude(coords[0]);
            storage.setLongitude(coords[1]);
            System.out.println("Geocoded address: " + storage.getAddress() + ", Coordinates: [" + coords[0] + ", " + coords[1] + "]");
        } else {
            System.err.println("Failed to geocode address: " + storage.getAddress());
            // Optionally, throw an exception or set default coordinates
            storage.setLatitude(0.0);
            storage.setLongitude(0.0);
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

        Optional<double[]> coordsOpt = geocodingService.geocode(storage.getAddress());
        if (coordsOpt.isPresent()) {
            double[] coords = coordsOpt.get();
            storage.setLatitude(coords[0]);
            storage.setLongitude(coords[1]);
            System.out.println("Geocoded address: " + storage.getAddress() + ", Coordinates: [" + coords[0] + ", " + coords[1] + "]");
        } else {
            System.err.println("Failed to geocode address: " + storage.getAddress());
            storage.setLatitude(0.0);
            storage.setLongitude(0.0);
        }

        return storageRepository.save(storage);
    }

    @Override
    public void save(Storage storage) {
        storageRepository.save(storage);
    }

    @Override
    public void deleteStorageById(int id) {
        storageRepository.deleteById(id);
    }

    @Override
    public long countAvailableStorages() {
        return storageRepository.countByStatus(true);
    }

    @Override
    public long countRentedStorages() {
        return storageRepository.countByStatus(false);
    }

    @Override
    public List<Storage> findAvailableStorages(
            LocalDate startDate, LocalDate endDate,
            Double minArea, Double minPrice,
            Double maxPrice, String nameKeyword, String city) {
        return storageRepository.findAvailableStorages(
                        startDate, endDate, minArea, minPrice, maxPrice, nameKeyword, city
                ).stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllCities() {
        return storageRepository.findAllCities();
    }
}