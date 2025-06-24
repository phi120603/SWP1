package com.example.swp.controller.website;

import com.cloudinary.Cloudinary;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.repository.OrderRepository;
import com.example.swp.service.CloudinaryService;
import com.example.swp.service.CustomerService;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/SWP/staff")
public class StaffStorageController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    CustomerService customerService;
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    OrderService orderService;
    @GetMapping("/staff-dashboard")
    public String showDashboard(Model model) {
        List<Storage> storages = storageService.getAll();
        int totalStorages = storages.size();

        List<Customer> customers = customerService.getAll();
        int totalUser = customers.size();

        List<Order> orders=orderService.getAllOrders();
        int totalOrders = orders.size();

        double totalRevenue = orderRepository.calculateTotalRevenue();


        model.addAttribute("storages", storages);
        model.addAttribute("totalStorages", totalStorages);
        model.addAttribute("customers", customers);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "/staff-dashboard";
    }

    @GetMapping("/customer-list")
    public String showUserList(Model model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "customer-list"; // Trang HTML hiển thị danh sách người dùng
    }

    @GetMapping("/addstorage")
    public String showAddStorageForm(Model model) {
        model.addAttribute("storage", new Storage());
        return "addstorage"; // Trang HTML chứa form
    }

    @GetMapping("/storages/{id}/detail")
    public String showStorageDetail(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isPresent()) {
            model.addAttribute("storage", optionalStorage.get());
            return "staff-storage-detail"; // Tên file Thymeleaf
        } else {
            redirectAttributes.addFlashAttribute("message", "Kho không tồn tại!");
            return "redirect:/SWP/staff/staff-dashboard"; // Điều hướng về dashboard nếu không tìm thấy
        }
    }

    @PostMapping("/addstorage")
    public String addStorage(@ModelAttribute StorageRequest storageRequest,
                             @RequestParam("image") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            // Upload ảnh
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                storageRequest.setImUrl(imageUrl);
            }

            // Gọi service lưu vào DB
            storageService.createStorage(storageRequest);
            redirectAttributes.addFlashAttribute("message", "Thêm kho thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi thêm kho.");
        }
        return "redirect:/SWP/storages"; // Điều hướng sau khi thêm
    }


    @PostMapping("/storages/{id}/delete")
    public String deleteStorage(@PathVariable int id, RedirectAttributes redirectAttributes) {
        storageService.deleteStorageById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xoá kho thành công!");
        return "redirect:/SWP/manager-dashboard"; // hoặc "/SWP/storages" nếu bạn có
    }
}
