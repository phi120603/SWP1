package com.example.swp.service.impl;

import com.example.swp.dto.OrderRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Order;
import com.example.swp.entity.Storage;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.OrderRepository;
import com.example.swp.repository.StorageRepository;
import com.example.swp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service // Thay đổi từ @Component sang @Service
@Transactional // QUAN TRỌNG: Thêm annotation này để quản lý transaction
@Slf4j // Thêm để logging
public class OrderServiceImpl implements OrderService { // Sửa tên class

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StorageRepository storageRepository; // Sửa tên biến cho đúng

    @Autowired
    private CustomerRepository customerRepository;

    // Bỏ inject StorageRequest vì đây là DTO, không phải Bean

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional // Đảm bảo method này chạy trong transaction
    public Order createOrder(OrderRequest orderRequest) {
        log.info("Đang tạo order cho customer ID: {}", orderRequest.getCustomerId());

        try {
            // Validate input
            if (orderRequest.getStartDate().isAfter(orderRequest.getEndDate())) {
                throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
            }

            // Tìm customer
            Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + orderRequest.getCustomerId()));

            // Tìm storage
            Storage storage = storageRepository.findById(orderRequest.getStorageId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy storage với ID: " + orderRequest.getStorageId()));

            // Tạo order mới
            Order order = new Order();
            order.setStartDate(orderRequest.getStartDate());
            order.setEndDate(orderRequest.getEndDate());
            order.setOrderDate(orderRequest.getOrderDate());

            // Tính số ngày thuê
            long rentalDays = ChronoUnit.DAYS.between(orderRequest.getStartDate(), orderRequest.getEndDate()) + 1;
            if (rentalDays <= 0) {
                rentalDays = 1; // Tối thiểu 1 ngày
            }

            // Tính tổng tiền
            double dailyRate = storage.getPricePerDay();
            double totalAmount = rentalDays * dailyRate;
            order.setTotalAmount(totalAmount);
            order.setStatus(orderRequest.getStatus());
            order.setCustomer(customer);
            order.setStorage(storage);

            // Lưu order
            Order savedOrder = orderRepository.save(order);

            // Force flush để đảm bảo dữ liệu được ghi xuống DB ngay lập tức
            orderRepository.flush();

            log.info("Tạo order thành công với ID: {}, Total Amount: {}", savedOrder.getId(), savedOrder.getTotalAmount());
            return savedOrder;

        } catch (Exception e) {
            log.error("Lỗi khi tạo order: ", e);
            throw new RuntimeException("Không thể tạo order: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Order save(Order order) {
        log.info("Đang lưu order với ID: {}", order.getId());
        Order savedOrder = orderRepository.save(order);
        orderRepository.flush(); // Đảm bảo dữ liệu được ghi xuống DB
        return savedOrder;
    }

    // Hàm tính total amount với BigDecimal (chuẩn hơn cho tiền tệ)
    public BigDecimal calculateTotalAmount(LocalDate startDate, LocalDate endDate, BigDecimal pricePerDay) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            days = 1; // Tối thiểu 1 ngày
        }
        return pricePerDay.multiply(BigDecimal.valueOf(days));
    }
}