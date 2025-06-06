package com.example.swp.controller.website;

import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}