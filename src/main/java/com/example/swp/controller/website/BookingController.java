package com.example.swp.controller.website;

import com.example.swp.entity.*;
import com.example.swp.enums.RoleName;
import com.example.swp.service.ContractService;
import com.example.swp.enums.VoucherStatus;
import com.example.swp.service.*;
import com.example.swp.util.HaversineUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/SWP/booking")
public class BookingController {
    @Autowired
    private ContractService contractService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherUsageService voucherUsageService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private HaversineUtil haversineUtil;

    @GetMapping("/search")
    public String showBookingSearchForm(Model model, HttpSession session) {
        model.addAttribute("order", new Order());
        model.addAttribute("cities", storageService.findAllCities());
        model.addAttribute("citySelected", null);
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
            @RequestParam(value = "userAddress", required = false) String userAddress,
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
                    .filter(storage -> orderService.countOverlapOrdersByCustomer(
                            customer.getId(),
                            storage.getStorageid(),
                            startDate,
                            endDate
                    ) == 0)
                    .collect(Collectors.toList());
        }

        Map<Integer, Double> remainAreas = new HashMap<>();
        for (Storage s : storages) {
            double remain = orderService.getRemainArea(s.getStorageid(), startDate, endDate);
            remainAreas.put(s.getStorageid(), remain);
        }

        // Sort theo địa chỉ nếu có
        double[] userCoordinates;
        if (userAddress != null && !userAddress.trim().isEmpty()) {
            Optional<double[]> userCoordinatesOpt = geocodingService.geocode(userAddress);
            if (userCoordinatesOpt.isPresent()) {
                userCoordinates = userCoordinatesOpt.get();
                System.out.println("Geocoding successful for address: " + userAddress + ", Coordinates: [" + userCoordinates[0] + ", " + userCoordinates[1] + "]");

                // Sort by distance
                storages = storages.stream()
                        .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                        .sorted(Comparator.comparingDouble(s -> {
                            double distance = haversine(userCoordinates[0], userCoordinates[1], s.getLatitude(), s.getLongitude());
                            System.out.println("Storage: " + s.getStoragename() + ", Distance: " + distance + " km");
                            return distance;
                        }))
                        .collect(Collectors.toList());
            } else {
                userCoordinates = null;
                System.out.println("Geocoding failed for address: " + userAddress);
                model.addAttribute("error", "Không thể định vị địa chỉ bạn nhập. Sử dụng sắp xếp mặc định.");
            }
        } else {
            userCoordinates = null;
            if (sortOption != null && !sortOption.isEmpty()) {
                // Apply alternative sorting
                switch (sortOption) {
                    case "priceAsc" -> storages.sort(Comparator.comparing(Storage::getPricePerDay));
                    case "priceDesc" -> storages.sort(Comparator.comparing(Storage::getPricePerDay).reversed());
                    case "areaAsc" -> storages.sort(Comparator.comparing(Storage::getArea));
                    case "areaDesc" -> storages.sort(Comparator.comparing(Storage::getArea).reversed());
                    case "nameAsc" -> storages.sort(Comparator.comparing(Storage::getStoragename, String.CASE_INSENSITIVE_ORDER));
                    case "nameDesc" -> storages.sort(Comparator.comparing(Storage::getStoragename, String.CASE_INSENSITIVE_ORDER).reversed());
                    default -> System.out.println("Invalid sortOption: " + sortOption);
                }
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
        model.addAttribute("userAddress", userAddress);
        model.addAttribute("userCoordinates", userCoordinates);

        return "booking-list";
    }

    @GetMapping("/{storageId}/booking")
    public String showBookingForm(@PathVariable int storageId,
                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  HttpSession session,
                                  Model model,
                                  @ModelAttribute("successMessage") String successMessage) {

        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            // ✨ Ghi URL cần redirect sau khi login
            String redirectUrl = "/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
            session.setAttribute("redirectAfterLogin", redirectUrl);
            return "redirect:/api/login";
        }

        // Nếu đặt kho thành công rồi thì về lại danh sách đơn
        if (successMessage != null && !successMessage.isBlank()) {
            return "redirect:/SWP/customers/my-bookings";
        }

        Optional<Storage> optionalStorage = storageService.findByID(storageId);
        if (optionalStorage.isEmpty()) {
            return "redirect:/SWP/booking/search";
        }
        Storage storage = optionalStorage.get();
        customer = (Customer) session.getAttribute("loggedInCustomer");


        List<Voucher> validVouchers = voucherService.getAllVouchers().stream()
                .filter(v -> v.getStatus() == VoucherStatus.ACTIVE)
                .filter(v -> v.getRemainQuantity() != null && v.getRemainQuantity() > 0)
                .toList();

        model.addAttribute("vouchers", validVouchers);

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
                                 @RequestParam(value = "voucherId", required = false) Integer voucherId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            // ✨ Ghi lại redirect URL để sau khi login quay lại
            String redirectUrl = "/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
            session.setAttribute("redirectAfterLogin", redirectUrl);
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đặt kho.");
            return "redirect:/api/login";
        }

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
        boolean isAvailable = orderService.isStorageAvailable(storageId, startDate, endDate);
        if (!isAvailable) {
            redirectAttributes.addFlashAttribute("error",
                    "Kho đã được đặt bởi người khác trong thời gian này. Vui lòng chọn thời gian khác.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }
        // Kiểm tra diện tích còn lại
        double remainArea = orderService.getRemainArea(storageId, startDate, endDate);
        if (rentalArea <= 0 || rentalArea > remainArea) {
            redirectAttributes.addFlashAttribute("error",
                    "Diện tích thuê phải lớn hơn 0 và không vượt quá diện tích còn trống (" + remainArea + " m²) trong suốt thời gian đặt.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // Kiểm tra trùng đơn đặt
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
        if (days <= 0) {
            days = 1;
        }

// ✅ Tính tổng tiền gốc
        double totalAmount = days * storage.getPricePerDay() * rentalArea / storage.getArea();
        double finalAmount = totalAmount;

// ✅ Áp dụng voucher nếu có
        Voucher voucher = null;
        if (voucherId != null) {
            Optional<Voucher> optionalVoucher = voucherService.getVoucherById(voucherId);
            if (optionalVoucher.isPresent()) {
                voucher = optionalVoucher.get();

                boolean usable = voucher.getStatus() == VoucherStatus.ACTIVE
                        && voucher.getEndDate() != null && voucher.getEndDate().isAfter(LocalDateTime.now())
                        && voucher.getRemainQuantity() != null && voucher.getRemainQuantity() > 0
                        && voucher.getRequiredPoint() != null && voucher.getRequiredPoint() <= customer.getPoints();

                if (!usable) {
                    redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ hoặc không thể sử dụng.");
                    return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
                }

                // Trừ điểm và voucher
                customer.setPoints(customer.getPoints() - voucher.getRequiredPoint());
                voucher.setRemainQuantity(voucher.getRemainQuantity() - 1);
                customerService.save(customer);
                voucherService.saveVoucher(voucher);

                // ✅ Trừ giảm giá
                finalAmount -= voucher.getDiscountAmount().doubleValue();
                if (finalAmount < 0) {
                    finalAmount = 0.0;
                }
            }
        }

// ✅ Tạo đơn hàng
        Order order = new Order();
        order.setCustomer(customer);
        order.setStorage(storage);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        order.setRentalArea(rentalArea);

// ✅ Dùng finalAmount duy nhất
        order.setTotalAmount(finalAmount);
        if (voucher != null) {
            order.setVoucher(voucher);
        }

        Order savedOrder = orderService.save(order);
        contractService.createContractForOrder(savedOrder);
        storageService.updateStatusBasedOnAvailability(storageId, startDate, endDate);


        // Ghi log lịch sử sử dụng voucher
        if (voucher != null) {
            VoucherUsage history = new VoucherUsage();
            history.setCustomer(customer);
            history.setVoucher(voucher);
            history.setOrder(savedOrder);
            history.setUsedAt(LocalDateTime.now());
            history.setDiscountAmount(BigDecimal.valueOf(voucher.getDiscountAmount().doubleValue())); // hoặc totalAmount - finalAmount

            voucherUsageService.save(history);
        }


        redirectAttributes.addFlashAttribute("successMessage",
                "Đặt kho thành công! Mã đơn #" + savedOrder.getId());
        return "redirect:/SWP/booking/my-bookings";
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

    @GetMapping("/my-bookings")
    public String viewMyBookings(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/SWP/login";
        }
        List<Order> orders = orderService.findOrdersByCustomer(customer);
        model.addAttribute("orders", orders);
        model.addAttribute("customer", customer);
        return "my-bookings";
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính trái đất (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // trả về km
    }
}
