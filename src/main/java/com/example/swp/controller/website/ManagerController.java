package com.example.swp.controller.website;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.swp.config.CloudinaryConfig;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Staff;
import com.example.swp.entity.Storage;
import com.example.swp.service.CloudinaryService;
import com.example.swp.service.CustomerService;
import com.example.swp.service.StaffService;
import com.example.swp.service.StorageService;
import com.example.swp.service.impl.CustomerServiceImpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/SWP")
public class ManagerController {

    @Autowired
    StorageService storageService;
    @Autowired
    CustomerService customerService;
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    StaffService staffService;


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

        List<Staff> staffs = staffService.getAllStaff();
        int totalStaff = staffs.size();

        model.addAttribute("storages", storages);
        model.addAttribute("totalStorages", totalStorages);
        model.addAttribute("customers", customers);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("staffs", staffs);
        model.addAttribute("totalStaff", totalStaff);

        return "admin";
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

    @GetMapping("/manager-dashboard/storages/{id}")
    public String viewStorageDetail(@PathVariable int id, Model model) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isPresent()) {
            model.addAttribute("storage", optionalStorage.get());
        } else {
            return "redirect:/SWP/manager-dashboard";
        }
        return "manager-storagedetail";
    }

    @GetMapping("/manager-dashboard/storages/{id}/edit")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isPresent()) {
            model.addAttribute("storage", optionalStorage.get());
        } else {
            return "redirect:/SWP/manager-dashboard";
        }
        return "manager-storage-edit"; // HTML trang sửa
    }
//edit storage
    @PutMapping("/manager-dashboard/storages/{id}")
    public String updateStorage(@PathVariable int id,
                                RedirectAttributes redirectAttributes,
                                @ModelAttribute StorageRequest storageRequest) {
        Optional<Storage> optional = storageService.findByID(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho!");
            return "redirect:/SWP/manager-dashboard";
        }

        storageService.updateStorage(storageRequest, optional.get());
        redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");

        // ✅ Sau khi cập nhật xong → quay về dashboard
        return "manager-storagedetail";
    }

    @PostMapping("/manager-dashboard/storages/{id}")
    public String updateStoragePost(@PathVariable int id,
                                    RedirectAttributes redirectAttributes,
                                    @ModelAttribute StorageRequest storageRequest,
                                    Model model) {
        Optional<Storage> optional = storageService.findByID(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho!");
            return "redirect:/SWP/manager-dashboard";
        }
        Storage updated = storageService.updateStorage(storageRequest, optional.get());
        model.addAttribute("storage", updated);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
        return "manager-storagedetail";
    }

    //danh sách staff
    @GetMapping("/staff-list")
    public String showStaffList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        Page<Staff> staffPage = staffService.getStaffsByPage(page - 1, size);

        int totalStaff = staffService.countAllStaff();

        model.addAttribute("staffPage", staffPage);
        model.addAttribute("staffs", staffPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("totalStaff", totalStaff);

        return "staff-list";
    }

    @GetMapping("/staff-list/edit/{id}")
    public String showEditStaffForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Staff> staffOpt = staffService.findById(id);
        if (staffOpt.isPresent()) {
            model.addAttribute("staff", staffOpt.get());
            return "edit-staff";
        } else {
            redirectAttributes.addFlashAttribute("error", "Staff not found!");
            return "redirect:/SWP/staff-list";
        }
    }

    @PostMapping("/staff-list/edit/{id}")
    public String editStaff(
            @PathVariable int id,
            @ModelAttribute("staff") Staff staff,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Staff> staffOpt = staffService.findById(id);
        if (staffOpt.isPresent()) {
            Staff existingStaff = staffOpt.get();
            existingStaff.setFullname(staff.getFullname());
            existingStaff.setEmail(staff.getEmail());
            existingStaff.setPhone(staff.getPhone());
            existingStaff.setRoleName(staff.getRoleName());
            existingStaff.setIdCitizenCard(staff.getIdCitizenCard());


            staffService.save(existingStaff);
            redirectAttributes.addFlashAttribute("message", "Cập nhật staff thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Staff not found!");
        }
        return "redirect:/SWP/staff-list";
    }

    @PostMapping("/storages/{id}/delete")
    public String deleteStorage(@PathVariable int id, RedirectAttributes redirectAttributes) {
        storageService.deleteStorageById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xoá kho thành công!");
        return "redirect:/SWP/manager-dashboard"; // hoặc "/SWP/storages" nếu bạn có
    }
}




