package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.enums.RoleName;
import com.example.swp.service.CustomerService;
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
    public String createCustomer(@ModelAttribute Customer customer) {
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
    public String updateCustomer(@PathVariable int id, @ModelAttribute Customer updatedCustomer) {
        Customer existing = customerService.getCustomer(id);
        if (existing == null) return "redirect:/SWP/customers?notfound=true";
        existing.setFullname(updatedCustomer.getFullname());
        existing.setAddress(updatedCustomer.getAddress());
        existing.setPhone(updatedCustomer.getPhone());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setRoleName(updatedCustomer.getRoleName());
        existing.setId_citizen(updatedCustomer.getId_citizen());
        // Password update nếu muốn
        if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
            existing.setPassword(updatedCustomer.getPassword());
        }
        customerService.save(existing);
        return "redirect:/SWP/customers/" + id;
    }

    // Xoá khách hàng
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id) {
        customerService.delete(id);
        return "redirect:/SWP/customers";
    }
}
