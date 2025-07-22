package com.example.swp.controller.website;

import com.example.swp.entity.Order;
import com.example.swp.entity.StorageItem;
import com.example.swp.repository.OrderRepository;
import com.example.swp.service.StorageItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer/storage-items")
public class CustomerStorageItemController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StorageItemService storageItemService;

    @GetMapping("/{orderId}")
    public String listItems(@PathVariable int orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStorage() == null) return "redirect:/error";

        double totalVolume = order.getStorage().getArea();

        List<StorageItem> items = storageItemService.getStorageItemsByOrder(order);
        double totalUsedVolume = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getVolumePerUnit())
                .sum();

        double emptyVolume = totalVolume - totalUsedVolume;
        double percentEmpty = (emptyVolume / totalVolume) * 100;

        model.addAttribute("items", items);
        model.addAttribute("order", order);
        model.addAttribute("totalVolume", totalVolume);
        model.addAttribute("totalUsedVolume", totalUsedVolume);
        model.addAttribute("emptyVolume", emptyVolume);
        model.addAttribute("percentEmpty", percentEmpty);
        model.addAttribute("storage", order.getStorage());

        return "customer-storage-item-list";
    }


    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        StorageItem item = storageItemService.getStorageItemById(id);
        if (item == null) return "redirect:/error";

        model.addAttribute("storageItem", item);
        return "customer-storage-item-form";
    }

    @GetMapping("/add/{orderId}")
    public String addForm(@PathVariable int orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return "redirect:/error";

        StorageItem item = new StorageItem();
        item.setOrder(order);
        model.addAttribute("storageItem", item);
        return "customer-storage-item-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute StorageItem storageItem) {
        if (storageItem.getOrder() == null || storageItem.getOrder().getId() == 0) {
            throw new IllegalArgumentException("Thiếu thông tin Order khi lưu StorageItem");
        }

        if (storageItem.getDateStored() == null) {
            storageItem.setDateStored(LocalDate.now());
        }

        storageItemService.saveStorageItem(storageItem);
        return "redirect:/customer/storage-items/" + storageItem.getOrder().getId();
    }


    @GetMapping("/delete/{id}/{orderId}")
    public String delete(@PathVariable int id, @PathVariable int orderId) {
        storageItemService.deleteStorageItem(id);
        return "redirect:/customer/storage-items/" + orderId;
    }
}

