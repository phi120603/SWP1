package com.example.swp.controller.website;

import com.example.swp.entity.Storage;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/SWP")
public class ManagerController {

    @Autowired
    StorageService storageService;

//    @GetMapping("/manager-dashboard")
//    public String showDashboard(Model model) {
//        model.addAttribute("pageTitle", "Dashboard");
//        return "admin";
//    }
    @GetMapping("/manager-dashboard")
    public String listStorages(Model model) {
        List<Storage> storages = storageService.getAll();
        int totalStorages = storages.size();
        model.addAttribute("storages", storages);
        model.addAttribute("totalStorages", totalStorages);
        return "admin";
    }

    @GetMapping("/addstorage")
    public String showAddStorageForm(Model model) {
        model.addAttribute("storage", new Storage());
        return "addstorage"; // trỏ tới file add-storage.html trong templates
    }


    @PostMapping("/addstorage")
    public String addStorage(Storage storage, Model model) {
        storageService.save(storage);
        model.addAttribute("message", "Storage added successfully!");
        return "redirect:/SWP/manager-dashboard";
    }
}
