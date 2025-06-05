package com.example.swp.controller.website;

import com.example.swp.entity.Storage;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/SWP")
public class StorageListController {
    @Autowired
    private StorageService storageService;

    @GetMapping("/storages")
    public String listStorage(Model model) {
        List<Storage> storages = storageService.getAll();
        System.out.println("Storages to display: " + storages); // Debug log
        model.addAttribute("storages", storages); // Fixed to "storages"
        return "storage-list";
    }
}