package com.example.swp.controller.api;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Storage;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/storage")
public class StorageController {
    @Autowired
    private StorageService storageService;
    @GetMapping ("/viewstorage")
    public List<Storage> viewStorage() {
        return storageService.getAll();
    }

    @PostMapping("/addstorage")
    public ResponseEntity<Storage> addStorage(@RequestBody StorageRequest storageRequest) {
        Storage storage = storageService.createStorage(storageRequest);
        return ResponseEntity.ok(storage);
    }

    @PutMapping("/updatestorage/{id}")
    public ResponseEntity<Storage> updateStorage(@PathVariable int id, @RequestBody StorageRequest storageRequest) {
        //check
        Optional<Storage> findStorageByID = storageService.findByID(id);
        if (findStorageByID.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Storage storage = storageService.updateStorage(storageRequest, findStorageByID.get());
        return ResponseEntity.ok(storage);
    }
    @GetMapping("/findByID/{id}")
    public Optional<Storage> findById(@PathVariable int id) {
        return storageService.findByID(id);
    }
}
