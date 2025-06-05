package com.example.swp.controller.api;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Storage;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Thêm import này
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller // Thay @RestController thành @Controller
@RequestMapping("/api/storages")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @GetMapping("/viewstorage")
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

    @GetMapping("/{id}/booking")
    public String showBookingPage(@PathVariable int id, Model model) {
        Optional<Storage> storageOpt = storageService.findByID(id);
        if (storageOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy kho với ID: " + id);
            return "error"; // Cần tạo template error.html nếu chưa có
        }
        model.addAttribute("storage", storageOpt.get());
        return "booking";
    }
}