package com.example.swp.controller.website;

import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;
import com.example.swp.service.CustomerService;
import com.example.swp.service.StorageService;
import com.example.swp.service.impl.CustomerServiceImpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/SWP")
public class ManagerController {

    @Autowired
    StorageService storageService;
    @Autowired
    CustomerService customerService;

    //    @GetMapping("/manager-dashboard")
//    public String showDashboard(Model model) {
//        model.addAttribute("pageTitle", "Dashboard");
//        return "admin";
//    }
    @GetMapping("/manager-dashboard")
    public String showDashboard(Model model) {
        List<Storage> storages = storageService.getAll();
        int totalStorages = storages.size();

        List<Customer> customers = customerService.getAll();
        int totalUser = customers.size();

        model.addAttribute("storages", storages);
        model.addAttribute("totalStorages", totalStorages);
        model.addAttribute("customers", customers);
        model.addAttribute("totalUser", totalUser);

        return "admin";
    }


    @GetMapping("/addstorage")
    public String showAddStorageForm(Model model) {
        model.addAttribute("storage", new Storage());
        return "addstorage"; // Trang HTML chứa form
    }

    @PostMapping("/addstorage")
    public String addStorage(@ModelAttribute("storage") Storage storage, RedirectAttributes redirectAttributes) {
        storageService.save(storage); // hoặc createStorage nếu bạn có method đó
        redirectAttributes.addFlashAttribute("message", "Thêm kho thành công!");
        return "redirect:storages"; // hoặc redirect sang trang danh sách nếu cần
    }


}
