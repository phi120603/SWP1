package com.example.swp.service.impl;

import com.example.swp.dto.OrderRequest;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.OrderRepository;
import com.example.swp.repository.StorageRepository;
import com.example.swp.service.OrderService;
import com.example.swp.service.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class OrderServiceimpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private StorageRequest storageRequest;
    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(int id) {return orderRepository.findById(id);}


    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + orderRequest.getCustomerId()));
        Storage storage = storageRepository.findById(orderRequest.getStorageId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho với ID: " + orderRequest.getStorageId()));

        Order order = new Order();
        order.setStartDate(orderRequest.getStartDate());
        order.setEndDate(orderRequest.getEndDate());
        order.setOrderDate(orderRequest.getOrderDate());

        // --- Phần thay đổi logic tính toán giá tiền (sử dụng double) ---
        LocalDate startDate = orderRequest.getStartDate();
        LocalDate endDate = orderRequest.getEndDate();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }

        // Tính số ngày thuê: Ngày kết thúc - Ngày bắt đầu + 1
        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Lấy giá mỗi ngày của kho (giả sử Storage.pricePerDay là double)
        double pricePerDay = storage.getPricePerDay();

        // Tính tổng tiền thuê
        double totalAmount = rentalDays * pricePerDay; // Phép tính dùng double
        order.setTotalAmount(totalAmount); // Gán giá trị double cho trường double
        // --- Kết thúc phần thay đổi logic tính toán giá tiền ---

        order.setStatus(orderRequest.getStatus());
        order.setCustomer(customer);
        order.setStorage(storage);
        return orderRepository.save(order);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    // Hàm calculateTotalAmount này có thể bỏ nếu không dùng ở nơi khác
    // Nếu giữ lại, nó cũng cần dùng double
    public double calculateTotalAmount(LocalDate startDate, LocalDate endDate, double pricePerDay) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return days * pricePerDay;


    }

}
