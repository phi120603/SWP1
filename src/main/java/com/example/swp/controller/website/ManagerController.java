package com.example.swp.controller.website;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.swp.config.CloudinaryConfig;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.*;
import com.example.swp.repository.FeedbackRepository;
import com.example.swp.repository.OrderRepository;
import com.example.swp.service.*;
import com.example.swp.service.impl.CustomerServiceImpl;
import com.example.swp.service.impl.StaffServiceimpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class ManagerController {

    @Autowired
    OrderService orderService;

    @Autowired
    FeedbackRepository feedbackRepository;
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
    private StaffService staffService;

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

        List<Staff> staff = staffService.getAllStaff();
        int totalStaff = staff.size();

        List<Order> latestOrders = orderService.getLast5orders();


        double totalRevenue = orderRepository.calculateTotalRevenue();


        model.addAttribute("storages", storages);
        model.addAttribute("totalStorages", totalStorages);
        model.addAttribute("customers", customers);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("staff", staff);
        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("latestOrders", latestOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("userName", userDetails.getUsername());
            model.addAttribute("userRole", auth.getAuthorities().iterator().next().getAuthority());
        }

        return "admin";
    }


    @GetMapping("/manager-customer-list")
    public String showUserList(Model model) {
        List<Customer> customers = customerService.getAll();
        int totalCustomers = customers.size();
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("customers", customers);
        return "manager-customer-list"; // Trang HTML hiển thị danh sách người dùng
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
            return "redirect:/admin/manager-dashboard";
        }
        return "manager-storagedetail";
    }

    @GetMapping("/manager-dashboard/storages/{id}/edit")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isPresent()) {
            model.addAttribute("storage", optionalStorage.get());
        } else {
            return "redirect:/admin/manager-dashboard";
        }
        return "manager-storage-edit"; // HTML trang sửa
    }


    @PostMapping("/storages/{id}/delete")
    public String deleteStorage(@PathVariable int id, RedirectAttributes redirectAttributes) {
        storageService.deleteStorageById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xoá kho thành công!");
        return "redirect:/admin/manager-dashboard";
    }

    //edit storage
    @PutMapping("/manager-dashboard/storages/{id}")
    public String updateStorage(@PathVariable int id,
                                RedirectAttributes redirectAttributes,
                                @ModelAttribute StorageRequest storageRequest) {
        Optional<Storage> optional = storageService.findByID(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho!");
            return "redirect:/admin/manager-dashboard";
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
            return "redirect:/admin/manager-dashboard";
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
            @RequestParam(defaultValue = "5") int size
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
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên!");
            return "redirect:/admin/staff-list";
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
            existingStaff.setIdCitizenCard(staff.getIdCitizenCard());


            staffService.save(existingStaff);
            redirectAttributes.addFlashAttribute("message", "Cập nhật staff thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên!");
        }
        return "redirect:/admin/staff-list";
    }
}





