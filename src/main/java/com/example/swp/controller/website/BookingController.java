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
        Map<Integer, Double> remainAreas = new HashMap<>();
        for (Storage s : storages) {
            double remain = orderService.getRemainArea(s.getStorageid(), startDate, endDate);
            remainAreas.put(s.getStorageid(), remain);
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
        model.addAttribute("remainAreas", remainAreas);
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

        // Tính diện tích còn lại để truyền xuống form cho khách nhập
        double remainArea = orderService.getRemainArea(storageId, startDate, endDate);
        model.addAttribute("remainArea", remainArea);

        // Token chống submit lại
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
                                 @RequestParam("rentalArea") double rentalArea,
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

        // Kiểm tra định dạng tên
        if (!name.matches("[\\p{L} .'-]{2,50}")) {
            redirectAttributes.addFlashAttribute("error", "Tên chỉ được chứa chữ, dấu cách và từ 2-50 ký tự.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            redirectAttributes.addFlashAttribute("error", "Email không đúng định dạng.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // Kiểm tra SĐT
        if (!phone.matches("^0[0-9]{9,10}$")) {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kho.");
            return "redirect:/SWP/booking/search";
        }
        Storage storage = optionalStorage.get();

        // Kiểm tra diện tích còn lại
        double remainArea = orderService.getRemainArea(storageId, startDate, endDate);
        if (rentalArea <= 0 || rentalArea > remainArea) {
            redirectAttributes.addFlashAttribute("error",
                    "Diện tích thuê phải lớn hơn 0 và không vượt quá diện tích còn trống (" + remainArea + " m²) trong suốt thời gian đặt.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // Kiểm tra trùng đơn đặt
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            Optional<Customer> existingCustomer = customerService.findByEmail1(email);
            if (existingCustomer.isPresent()) {
                customer = existingCustomer.get();
            } else {
                customer = new Customer();
                customer.setFullname(name);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setRoleName(RoleName.CUSTOMER);
                customer.setPassword("default-guest-password");
                customer.setId_citizen("GUEST-" + UUID.randomUUID().toString().substring(0, 13));
                customer = customerService.save(customer);
            }
            session.setAttribute("loggedInCustomer", customer);

        }
        long overlapCount = orderService.countOverlapOrdersByCustomer(
                customer.getId(),
                storageId,
                startDate,
                endDate
        );
        if (overlapCount > 0) {
            redirectAttributes.addFlashAttribute("error",
                    "Bạn đã từng đặt kho này trong khoảng thời gian này. Vui lòng kiểm tra lại đơn hàng hoặc chọn thời gian khác.");
            return "redirect:/SWP/booking/search";
        }

        // Tính tiền
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double totalAmount = (days > 0 ? days : 1) * storage.getPricePerDay() * rentalArea / storage.getArea();

        // Tạo mới Order (LƯU rentalArea)
        Order order = new Order();
        order.setCustomer(customer);
        order.setStorage(storage);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        order.setRentalArea(rentalArea);
        order.setTotalAmount(totalAmount);

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
