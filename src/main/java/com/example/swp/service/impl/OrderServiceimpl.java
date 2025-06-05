package com.example.swp.service.impl;

import com.example.swp.dto.OrderRequest;
import com.example.swp.dto.StorageRequest;
import com.example.swp.entity.Order;
import com.example.swp.repository.OrderRepository;
import com.example.swp.repository.StorageRepository;
import com.example.swp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

@Component
public class OrderServiceimpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private StorageRequest storageRequest;
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(int id) {return orderRepository.findById(id);}

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setStartDate(orderRequest.getStartDate());
        order.setEndDate(orderRequest.getEndDate());
        order.setOrderDate(orderRequest.getOrderDate());
        long rentalDays = ChronoUnit.DAYS.between((Temporal) orderRequest.getStartDate(), (Temporal) orderRequest.getEndDate());
        // Tính tổng tiền thuê
        double dailyRate = storageRequest.getPricePerDay(); // hoặc giá cố định
        double totalAmount = rentalDays * dailyRate;
        order.setTotalAmount(totalAmount);
        order.setStatus(orderRequest.getStatus());
        return orderRepository.save(order);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    //Hàm tính total amount
    public BigDecimal calculateTotalAmount(LocalDate startDate, LocalDate endDate, BigDecimal pricePerDay) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        return pricePerDay.multiply(BigDecimal.valueOf(days));


    }

//    public Order createOrder(OrderRequest dto) {
//        // Lấy thông tin kho
//        Storage storage = storageRepository.findById(dto.getStorageId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho"));
//
//        // Tính tổng tiền thuê (giá mỗi ngày * số ngày thuê)
//        BigDecimal totalAmount = calculateTotalAmount(dto.getStartDate(), dto.getEndDate(), storage.getPricePerDay());
//
//        // Khởi tạo đơn hàng
//        Order order = new Order();
//        order.setStartDate(dto.getStartDate());
//        order.setEndDate(dto.getEndDate());
//        order.setStorage(storage);
//
//        // Lấy thông tin khách hàng
//        Customer customer = customerRepository.findById(dto.getCustomerId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
//        order.setCustomer(customer);
//
//        // Gán ngày đặt hàng là thời điểm hiện tại
//        order.setOrderDate(LocalDate.now());
//
//        // Gán tổng tiền
//        order.setTotalAmount(totalAmount);
//
//        // Gán trạng thái đơn hàng ban đầu là "PENDING"
//        order.setStatus("PENDING");
//
//        // Lưu đơn hàng vào database
//        return orderRepository.save(order);
//    }

}
