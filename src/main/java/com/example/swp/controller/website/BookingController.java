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

    /**
     * Hiển thị form tìm kiếm booking
     */
    @GetMapping("/search")
    public String showBookingSearchForm(Model model, HttpSession session) {
        model.addAttribute("order", new Order());
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer != null) {
            model.addAttribute("customer", customer);
        }
        return "booking-search";
    }

    /**
     * Xử lý tìm kiếm kho còn trống
     */
    @GetMapping("/search/result")
    public String processBookingSearch(
            @ModelAttribute("order") Order searchOrder,
            @RequestParam(value = "minArea", required = false) Double minArea,
            Model model) {

        LocalDate startDate = searchOrder.getStartDate();
        LocalDate endDate = searchOrder.getEndDate();

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            model.addAttribute("error", "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            return "booking-search";
        }

        List<Storage> storages = storageService.findAvailableStorages(startDate, endDate, minArea);

        model.addAttribute("storages", storages);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("minArea", minArea);

        return "booking-list";
    }

    /**
     * Hiển thị form booking khi khách chọn kho
     */
    @GetMapping("/{storageId}/booking")
    public String showBookingForm(@PathVariable int storageId,
                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  HttpSession session,
                                  Model model,
                                  @ModelAttribute("successMessage") String successMessage) {

        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            return "redirect:/SWP/booking/search";
        }

        Storage storage = optionalStorage.get();
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        // TẠO TOKEN mới mỗi lần mở form
        String orderToken = UUID.randomUUID().toString();
        session.setAttribute("orderToken", orderToken);
        model.addAttribute("orderToken", orderToken);

        model.addAttribute("storage", storage);
        model.addAttribute("customer", customer);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        if (successMessage != null && !successMessage.isBlank()) {
            model.addAttribute("successMessage", successMessage);
        }

        return "booking-form";
    }

    /**
     * Xử lý submit booking
     */
    @PostMapping("/{storageId}/booking/save")
    public String processBooking(@PathVariable int storageId,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 @RequestParam("orderToken") String orderToken,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        // KIỂM TRA TOKEN
        String sessionToken = (String) session.getAttribute("orderToken");
        if (sessionToken == null || !sessionToken.equals(orderToken)) {
            redirectAttributes.addFlashAttribute("error", "Form không hợp lệ hoặc đã được submit.");
            return "redirect:/SWP/booking/search";
        }
        // Xoá token ngay sau khi dùng
        session.removeAttribute("orderToken");

        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho.");
            return "redirect:/SWP/booking/search";
        }

        Storage storage = optionalStorage.get();
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        if (customer == null) {
            Optional<Customer> existingCustomer = customerService.findByEmail(email);
            if (existingCustomer.isPresent()) {
                customer = existingCustomer.get();
            } else {
                customer = new Customer();
                customer.setFullname(name);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setRoleName(RoleName.CUSTOMER);
                customer.setPassword("default-guest-password");
                customer.setId_citizen("GUEST-" + UUID.randomUUID().toString());
                customer = customerService.save(customer);
            }
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setStorage(storage);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        order.setTotalAmount(days > 0 ? days * storage.getPricePerDay() : storage.getPricePerDay());

        Order savedOrder = orderService.save(order);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đặt kho thành công! Mã đơn #" + savedOrder.getId());

        return "redirect:/SWP/customers/my-bookings";
    }

    /**
     * Chi tiết đơn hàng
     */
    @GetMapping("/detail")
    public String bookingDetail(@RequestParam("orderId") int orderId, Model model) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy đơn hàng #" + orderId);
            return "error";
        }
        Order order = orderOpt.get();
        model.addAttribute("order", order);
        return "booking-detail";
    }
}
