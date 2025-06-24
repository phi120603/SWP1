package com.example.swp.controller.website;

import com.cloudinary.Cloudinary;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.entity.Feedback; // Thêm dòng này
import com.example.swp.service.CloudinaryService;
import com.example.swp.service.CustomerService;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import com.example.swp.service.FeedbackService; // Thêm dòng này
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
    StorageService storageService;
    @Autowired
    CustomerService customerService;
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    OrderService orderService;
    @Autowired
    FeedbackService feedbackService; // Thêm dòng này

    @GetMapping("/staff-dashboard")
    public String showDashboard(Model model) {
        List<Storage> storages = storageService.getAll();
        int totalStorages = storages.size();

        List<Customer> customers = customerService.getAll();
        int totalUser = customers.size();

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        // LẤY FEEDBACKS Ở ĐÂY
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        int totalFeedback = feedbacks.size();

        model.addAttribute("storages", storages);
        model.addAttribute("totalStorages", totalStorages);
        model.addAttribute("customers", customers);
        model.addAttribute("totalUser", totalUser);

        // TRUYỀN FEEDBACKS VÀO VIEW
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("totalFeedback", totalFeedback);

        return "staff-dashboard";
    }

    @GetMapping("/customer-list")
    public String showUserList(Model model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "customer-list";
    }

    @GetMapping("/addstorage")
    public String showAddStorageForm(Model model) {
        model.addAttribute("storage", new Storage());
        return "addstorage";
    }

    @GetMapping("/storages/{id}/detail")
    public String showStorageDetail(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isPresent()) {
            model.addAttribute("storage", optionalStorage.get());
            return "staff-storage-detail";
        } else {
            redirectAttributes.addFlashAttribute("message", "Kho không tồn tại!");
            return "redirect:/SWP/staff/staff-dashboard";
        }
    }

    @GetMapping("/staff-all-storage")
    public String showAllStorageList(Model model) {
        List<Storage> storages = storageService.getAll();
        model.addAttribute("storages", storages);
        return "staff-all-storage";
    }

    @PostMapping("/addstorage")
    public String addStorage(@ModelAttribute StorageRequest storageRequest,
                             @RequestParam("image") MultipartFile file,
                             @RequestParam("returnUrl") String returnUrl,
                             RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                storageRequest.setImUrl(imageUrl);
            }
            storageService.createStorage(storageRequest);
            redirectAttributes.addFlashAttribute("message", "Thêm kho thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi thêm kho.");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/storages/{id}/delete")
    public String deleteStorage(@PathVariable("id") int id,
                                @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                RedirectAttributes redirectAttributes) {
        storageService.deleteStorageById(id);
        redirectAttributes.addFlashAttribute("message", "Storage deleted successfully");
        if (returnUrl == null || returnUrl.isEmpty()) {
            return "redirect:/SWP/staff/staff-all-storage";
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/storages/{id}/edit")
    public String editStorage(@PathVariable int id,
                              @ModelAttribute Storage storage,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<Storage> existingStorageOpt = storageService.findByID(id);
            if (existingStorageOpt.isPresent()) {
                Storage existingStorage = existingStorageOpt.get();

                StorageRequest storageRequest = new StorageRequest();
                storageRequest.setStoragename(storage.getStoragename());
                storageRequest.setAddress(storage.getAddress());
                storageRequest.setState(storage.getState());
                storageRequest.setCity(storage.getCity());
                storageRequest.setDescription(storage.getDescription());

                storageRequest.setArea(existingStorage.getArea());
                storageRequest.setPricePerDay(existingStorage.getPricePerDay());
                storageRequest.setStatus(existingStorage.isStatus());
                storageRequest.setImUrl(existingStorage.getImUrl());

                storageService.updateStorage(storageRequest, existingStorage);

                redirectAttributes.addFlashAttribute("message", "Cập nhật kho thành công!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            } else {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy kho để cập nhật!");
                redirectAttributes.addFlashAttribute("messageType", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi cập nhật kho: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/SWP/staff/storages/" + id + "/detail";
    }
}
