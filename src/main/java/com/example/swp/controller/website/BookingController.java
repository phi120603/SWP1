package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.enums.RoleName;
import com.example.swp.service.CustomerService;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/SWP/booking")
public class BookingController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    // 1. Hiển thị form tìm kiếm booking (không liên kết storage nào)
    @GetMapping("/search")
    public String showBookingSearchForm(Model model, HttpSession session) {
        model.addAttribute("order", new Order());
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer != null) {
            model.addAttribute("customer", customer);
        }// Sử dụng Order cho binding ngày
        return "booking-search";
    }

    // 2. Xử lý submit form tìm kiếm, hiển thị danh sách kho đáp ứng
    @GetMapping("/search/result")
    public String processBookingSearch(@ModelAttribute("order") Order searchOrder, Model model) {
        LocalDate startDate = searchOrder.getStartDate();
        LocalDate endDate = searchOrder.getEndDate();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            model.addAttribute("error", "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            return "booking-search";
        }

        List<Storage> storages = storageService.findAvailableStorages(startDate, endDate);
        model.addAttribute("storages", storages);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "booking-list";
    }

    // 3. Hiển thị form booking khi khách chọn 1 kho từ danh sách
    @GetMapping("/{storageId}/booking")
    public String showBookingForm(@PathVariable int storageId,
                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  HttpSession session,
                                  Model model,
                                  @ModelAttribute("successMessage") String successMessage ) {
        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            return "redirect:/SWP/booking/search";
        }

        Storage storage = optionalStorage.get();
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        model.addAttribute("storage", storage);
        model.addAttribute("customer", customer); // Nếu đã login, có thể fill form sẵn
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        if (successMessage != null && !successMessage.isBlank()) {
            model.addAttribute("successMessage", successMessage);
        }

        return "booking-form"; // Form nhập thêm info khách nếu muốn
    }

    // 4. Xử lý submit booking, chỉ lúc này mới lưu vào DB!
    @PostMapping("/{storageId}/booking/save")
    public String processBooking(@PathVariable int storageId,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Validate input
        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // Check storage
        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho.");
            return "redirect:/SWP/booking/search";
        }

        Storage storage = optionalStorage.get();
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        // Handle guest user
        if (customer == null) {
            Optional<Customer> existingCustomer = customerService.findByEmail(email);
            if (existingCustomer.isPresent()) {
                customer = existingCustomer.get();
            } else {
                customer = new Customer();
                customer.setFullname(name);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setRoleName(RoleName.customer); // Use enum value instead of String
                customer.setPassword("default-guest-password"); // Adjust as needed
                customer.setId_citizen("GUEST-" + UUID.randomUUID().toString()); // Adjust as needed
                customer = customerService.save(customer); // Persist Customer to avoid TransientObjectException
            }
        }

        // Create and save order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStorage(storage);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        order.setTotalAmount(days > 0 ? days * storage.getPricePerDay() : storage.getPricePerDay());

        orderService.save(order);

        return "redirect:/SWP/booking/success?orderId=" + order.getId();




    }

    // 5. Trang xác nhận booking thành công
    @GetMapping("/success")
    public String bookingSuccess(@RequestParam("orderId") int orderId, Model model) {
        Order order = orderService.getOrderById(orderId).orElse(null);
        model.addAttribute("order", order);
        return "booking-success";
    }


}
