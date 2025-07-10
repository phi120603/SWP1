package com.example.swp.controller.website;

import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.entity.ViewingAppointment;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import com.example.swp.service.ViewingAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
@RequestMapping("/SWP")
public class StorageDetailController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ViewingAppointmentService viewingAppointmentService;

    @GetMapping("/storages/{id}")
    public String viewStorageDetail(@PathVariable int id, Model model) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isPresent()) {
            model.addAttribute("storage", optionalStorage.get());
        } else {
            return "redirect:/SWP/storages";
        }
        return "storage-detail";
    }

    // Hiển thị form booking
    @GetMapping("/storages/{id}/booking")
    public String showBookingForm(@PathVariable int id, Model model) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isEmpty()) {
            return "redirect:/SWP/storages";
        }
        model.addAttribute("storage", optionalStorage.get());
        return "booking";
    }

    // Xử lý submit booking
    @PostMapping("/storages/{id}/booking/save")
    public String processBooking(@PathVariable int id,
                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 Model model) {
        Optional<Storage> optionalStorage = storageService.findByID(id);
        if (optionalStorage.isEmpty()) {
            return "redirect:/SWP/storages";
        }

        Storage storage = optionalStorage.get();

        if (!endDate.isAfter(startDate)) {
            model.addAttribute("storage", storage);
            model.addAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu.");
            return "booking";
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double total = days * storage.getPricePerDay();

        Order order = new Order();
        order.setStorage(storage);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(total);
        order.setStatus("PENDING");

        orderService.save(order);

        return "redirect:/SWP/storages/" + id + "?message=Booking thành công!";
    }

    @GetMapping("/storages/compare")
    public String compareStorages(@RequestParam("id1") int id1,
                                  @RequestParam("id2") int id2,
                                  Model model) {
        Optional<Storage> s1 = storageService.findByID(id1);
        Optional<Storage> s2 = storageService.findByID(id2);

        if (s1.isEmpty() || s2.isEmpty()) {
            return "redirect:/SWP/storages"; // hoặc hiển thị lỗi
        }

        model.addAttribute("storage1", s1.get());
        model.addAttribute("storage2", s2.get());

        return "storage-compare"; // tên file html sẽ tạo bên dưới
    }

    @GetMapping("/storages/{id}/appointment")
    public String showAppointmentForm(@PathVariable int id, Model model) {
        Optional<Storage> optional = storageService.findByID(id);
        if (optional.isEmpty()) return "redirect:/SWP/storages";

        model.addAttribute("storage", optional.get());
        model.addAttribute("appointment", new ViewingAppointment());
        return "storage-appointment";
    }

    @PostMapping("/storages/{id}/appointment/save")
    public String processAppointment(@PathVariable int id,
                                     @ModelAttribute ViewingAppointment appointment,
                                     @RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
                                     Model model) {
        Optional<Storage> optional = storageService.findByID(id);
        if (optional.isEmpty()) return "redirect:/SWP/storages";

        appointment.setAppointmentDateTime(datetime);
        appointment.setStorage(optional.get());

        appointment.setId(0);
        viewingAppointmentService.save(appointment);

        String encodedMessage = URLEncoder.encode("Đặt lịch xem kho thành công!", StandardCharsets.UTF_8);
        return "redirect:/SWP/storages/" + id + "?message=" + encodedMessage;
    }
}