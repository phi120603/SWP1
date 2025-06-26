package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import com.example.swp.service.CustomerService;
import com.example.swp.validate.CustomerValidator; // <--- THÊM import này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/SWP/customers")
public class CustomerCRUDController {

    @Autowired
    private CustomerService customerService;

    // FORM tạo mới khách hàng
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("roles", RoleName.values());
        return "customer-create";
    }

    // Xử lý tạo mới
    @PostMapping("/create")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
        String error = CustomerValidator.validate(customer); // <--- validate dữ liệu
        if (error != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("roles", RoleName.values());
            model.addAttribute("error", error); // Truyền lỗi về view
            return "customer-create";
        }
        customerService.save(customer);
        return "redirect:/SWP/customers";
    }

    // FORM sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Customer customer = customerService.getCustomer(id);
        if (customer == null) return "redirect:/SWP/customers?notfound=true";
        model.addAttribute("customer", customer);
        model.addAttribute("roles", RoleName.values());
        return "customer-edit";
    }

    // Xử lý sửa
    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable int id, @ModelAttribute Customer updatedCustomer, Model model) {
        Customer existing = customerService.getCustomer(id);
        if (existing == null) return "redirect:/SWP/customers?notfound=true";

        // Cập nhật thông tin mới vào customer cũ
        existing.setFullname(updatedCustomer.getFullname());
        existing.setAddress(updatedCustomer.getAddress());
        existing.setPhone(updatedCustomer.getPhone());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setRoleName(updatedCustomer.getRoleName());
        existing.setId_citizen(updatedCustomer.getId_citizen());
        // Nếu có nhập password mới thì cập nhật
        if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
            existing.setPassword(updatedCustomer.getPassword());
        }

        String error = CustomerValidator.validate(existing); // <--- validate lại sau khi cập nhật
        if (error != null) {
            model.addAttribute("customer", existing);
            model.addAttribute("roles", RoleName.values());
            model.addAttribute("error", error);
            return "customer-edit";
        }

        customerService.save(existing);
        return "redirect:/SWP/customers/" + id;
    }

    // deactive khách hàng
    @PostMapping("/delete/{id}")
    public String deactivateCustomer(@PathVariable int id) {
        Customer customer = customerService.getCustomer(id);
        if (customer != null) {
            customer.setRoleName(RoleName.BLOCKED);
            customerService.save(customer);
        }
        return "redirect:/SWP/customers";
    }

}
