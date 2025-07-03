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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/SWP/booking")
public class BookingController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/search")
    public String showBookingSearchForm(Model model, HttpSession session) {
        model.addAttribute("order", new Order());
        model.addAttribute("cities", storageService.findAllCities()); // THÊM DÒNG NÀY
        model.addAttribute("citySelected", null); // hoặc "" nếu muốn
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer != null) {
            model.addAttribute("customer", customer);
        }
        return "booking-search";
    }


    @GetMapping("/search/result")
    public String processBookingSearch(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "minArea", required = false) Double minArea,
            @RequestParam(value = "nameKeyword", required = false) String nameKeyword,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "sortOption", required = false) String sortOption,
            Model model,
            HttpSession session) {

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            model.addAttribute("error", "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            return "booking-search";
        }

        List<Storage> storages = storageService.findAvailableStorages(
                startDate, endDate, minArea, minPrice, maxPrice, nameKeyword, city
        );

        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer != null) {
            storages = storages.stream()
                    .filter(storage ->
                            orderService.countOverlapOrdersByCustomer(
                                    customer.getId(),
                                    storage.getStorageid(),
                                    startDate,
                                    endDate
                            ) == 0
                    )
                    .collect(Collectors.toList());
        }

        // SORT giữ nguyên như cũ
        if (sortOption != null) {
            switch (sortOption) {
                case "priceAsc" -> storages.sort(Comparator.comparing(Storage::getPricePerDay));
                case "priceDesc" -> storages.sort(Comparator.comparing(Storage::getPricePerDay).reversed());
                case "areaAsc" -> storages.sort(Comparator.comparing(Storage::getArea));
                case "areaDesc" -> storages.sort(Comparator.comparing(Storage::getArea).reversed());
                case "nameAsc" -> storages.sort(Comparator.comparing(Storage::getStoragename, String.CASE_INSENSITIVE_ORDER));
                case "nameDesc" -> storages.sort(Comparator.comparing(Storage::getStoragename, String.CASE_INSENSITIVE_ORDER).reversed());
            }
        }

        model.addAttribute("storages", storages);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("minArea", minArea);
        model.addAttribute("nameKeyword", nameKeyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortOption", sortOption);
        model.addAttribute("citySelected", city);
        model.addAttribute("cities", storageService.findAllCities());

        return "booking-list";
    }



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

        // Sinh token chống submit lại
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

        // Kiểm tra token
        String sessionToken = (String) session.getAttribute("orderToken");
        if (sessionToken == null || !sessionToken.equals(orderToken)) {
            redirectAttributes.addFlashAttribute("error", "Form không hợp lệ hoặc đã được submit.");
            return "redirect:/SWP/booking/search";
        }
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

        // Kiểm tra overlap với các đơn khác
        boolean available = orderService.isStorageAvailable(storageId, startDate, endDate);
        if (!available) {
            redirectAttributes.addFlashAttribute("error", "Kho bạn chọn đã được người khác đặt trong thời gian này.");
            return "redirect:/SWP/booking/search";
        }

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
                customer.setId_citizen("GUEST-" + UUID.randomUUID());
                customer = customerService.save(customer);
            }
        }

        // Kiểm tra trùng đơn của chính user
        long overlapCount = orderService.countOverlapOrdersByCustomer(
                customer.getId(),
                storageId,
                startDate,
                endDate
        );

        if (overlapCount > 0) {
            redirectAttributes.addFlashAttribute("error",
                    "Bạn đã từng đặt kho này trong khoảng thời gian này. Vui lòng kiểm tra lại đơn hàng của bạn hoặc chọn thời gian khác.");
            return "redirect:/SWP/booking/search";
        }

        // Tạo mới Order
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
