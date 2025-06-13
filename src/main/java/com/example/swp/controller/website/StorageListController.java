package com.example.swp.controller.website;

import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/SWP")
public class StorageListController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/storages")
    public String listStorage(
            @RequestParam(required = false) String storageName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            Model model) {

        List<Storage> allStorages = storageService.getAll();
        System.out.println("All storages from database: " + allStorages.size());

        // Filter storages based on search criteria
        List<Storage> filteredStorages = filterStorages(allStorages, storageName, city, status);
        System.out.println("Filtered storages: " + filteredStorages.size());

        // Get unique cities for dropdown
        List<String> cities = allStorages.stream()
                .map(Storage::getCity)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("storages", filteredStorages);
        model.addAttribute("cities", cities);

        // Keep search parameters for form persistence
        model.addAttribute("searchStorageName", storageName);
        model.addAttribute("searchCity", city);
        model.addAttribute("searchStatus", status);

        return "storage-list";
    }

    private List<Storage> filterStorages(List<Storage> storages, String storageName, String city, String status) {
        return storages.stream()
                .filter(storage -> {
                    // Filter by storage name
                    if (storageName != null && !storageName.trim().isEmpty()) {
                        String searchName = storageName.trim().toLowerCase();
                        String actualName = storage.getStoragename() != null ?
                                storage.getStoragename().toLowerCase() : "";
                        if (!actualName.contains(searchName)) {
                            return false;
                        }
                    }

                    // Filter by city
                    if (city != null && !city.trim().isEmpty()) {
                        String actualCity = storage.getCity() != null ? storage.getCity() : "";
                        if (!actualCity.equals(city)) {
                            return false;
                        }
                    }

                    // Filter by status
                    if (status != null && !status.trim().isEmpty()) {
                        boolean searchStatus = Boolean.parseBoolean(status);
                        if (storage.isStatus() != searchStatus) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }
}