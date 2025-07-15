package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.EContract;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.enums.RoleName;
import com.example.swp.service.CustomerService;
import com.example.swp.service.EContractService;
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

    @Autowired
    private EContractService eContractService;

    // Trang search (nếu có)
    @GetMapping("/search")
    public String searchPage() {
        return "booking/search";
    }

    // Hiển thị form đặt kho
    @GetMapping("/{storageId}/booking")
    public String showBookingForm(
            @PathVariable int storageId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session,
            RedirectAttributes rd
    ) {
        Optional<Storage> opt = storageService.findByID(storageId);
        if (opt.isEmpty()) {
            rd.addFlashAttribute("error", "Không tìm thấy kho.");
            return "redirect:/SWP/booking/search";
        }
        Storage storage = opt.get();

        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null)   endDate   = startDate.plusDays(1);
        if (endDate.isBefore(startDate)) {
            rd.addFlashAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu.");
            return "redirect:/SWP/booking/search";
        }

        double remainArea = orderService.getRemainArea(storageId, startDate, endDate);
        String token = UUID.randomUUID().toString();
        session.setAttribute("orderToken", token);

        model.addAttribute("storage", storage);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("remainArea", remainArea);
        model.addAttribute("orderToken", token);
        model.addAttribute("customer", session.getAttribute("loggedInCustomer"));

        return "booking-form";
    }

    // Xử lý submit form đặt kho
    @PostMapping("/{storageId}/booking/save")
    public String processBooking(
            @PathVariable int storageId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam double rentalArea,
            @RequestParam String orderToken,
            HttpSession session,
            RedirectAttributes rd
    ) {
        // 1) Token chống double-submit
        String sessionToken = (String) session.getAttribute("orderToken");
        if (sessionToken == null || !sessionToken.equals(orderToken)) {
            rd.addFlashAttribute("error", "Form không hợp lệ hoặc đã submit rồi.");
            return "redirect:/SWP/booking/search";
        }
        session.removeAttribute("orderToken");

        // 2) Validate bắt buộc
        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            rd.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }
        if (!name.matches("[\\p{L} .'-]{2,50}")) {
            rd.addFlashAttribute("error", "Tên chỉ chứa chữ và dài 2-50 ký tự.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }
        if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            rd.addFlashAttribute("error", "Email không đúng định dạng.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }
        if (!phone.matches("^0[0-9]{9,10}$")) {
            rd.addFlashAttribute("error", "SĐT phải bắt đầu 0 và dài 10-11 chữ số.");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // 3) Lấy Storage
        Optional<Storage> opt = storageService.findByID(storageId);
        if (opt.isEmpty()) {
            rd.addFlashAttribute("error", "Không tìm thấy kho.");
            return "redirect:/SWP/booking/search";
        }
        Storage storage = opt.get();

        // 4) Kiểm tra diện tích còn
        double remainArea = orderService.getRemainArea(storageId, startDate, endDate);
        if (rentalArea <= 0 || rentalArea > remainArea) {
            rd.addFlashAttribute("error", "Diện tích thuê không hợp lệ. Tối đa còn " + remainArea + " m².");
            return "redirect:/SWP/booking/" + storageId + "/booking?startDate=" + startDate + "&endDate=" + endDate;
        }

        // 5) Lấy hoặc tạo Customer
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            Optional<Customer> ex = customerService.findByEmail(email);
            customer = ex.orElseGet(() -> {
                Customer c = new Customer();
                c.setFullname(name);
                c.setEmail(email);
                c.setPhone(phone);
                c.setRoleName(RoleName.CUSTOMER);
                c.setPassword("default-guest-password");
                String guestId = "GUEST-" + UUID.randomUUID();
                c.setId_citizen(guestId.length() > 20 ? guestId.substring(0, 20) : guestId);
                return customerService.save(c);
            });
            session.setAttribute("loggedInCustomer", customer);
        }

        // 6) Tính tiền và lưu Order
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double totalAmount = (days > 0 ? days : 1)
                * storage.getPricePerDay()
                * rentalArea
                / storage.getArea();

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

        // 7) Tạo hợp đồng điện tử
        EContract contract = EContract.builder()
                .title("Hợp đồng thuê kho #" + savedOrder.getId())
                .createdDate(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .signed(false)
                .terms("""
                        ➤ Khách hàng đồng ý tuân thủ quy định kho và thời gian thuê.
                        ➤ Diện tích thuê: %s m².
                        ➤ Tổng chi phí: %,.0f VNĐ.
                        ➤ Thời gian thuê: %s đến %s.
                        ➤ Việc ký hợp đồng là điều kiện để tiến hành thanh toán.
                        """.formatted(rentalArea, totalAmount, startDate, endDate))
                .customer(customer)
                .build();

        // Sau khi build thì gọi setter pricePerDay
        contract.setPricePerDay(storage.getPricePerDay());

        EContract savedContract = eContractService.createContract(contract);
        return "redirect:/econtract/view/" + savedContract.getId();

    }

}
