package com.example.swp.service.impl;

import com.example.swp.entity.Order;
import com.example.swp.repository.OrderRepository;
import com.example.swp.service.EmailService;
import com.example.swp.service.PendingRenewalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PendingRenewalOrderServiceImpl implements PendingRenewalOrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public List<Order> getPaidRenewalOrders(int days) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(days);
        return orderRepository.findPaidRenewalOrders(today, threshold);
    }

    @Override
    public void sendRenewalWarningEmail(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        long daysUntilDue = LocalDate.now().until(order.getEndDate()).getDays();
        String customerEmail = order.getCustomer().getEmail();
        String subject = "Nhắc nhở gia hạn đơn hàng #" + order.getId();
        String content = String.format(
                "Kính gửi %s,\n\n" +
                        "Đơn hàng #%d của quý khách sẽ hết hạn vào ngày %s (còn %d ngày). " +
                        "Vui lòng thực hiện thanh toán để gia hạn dịch vụ kho bãi.\n\n" +
                        "Trân trọng,\nHệ thống kho bãi",
                order.getCustomer().getFullname(), order.getId(), order.getEndDate(), daysUntilDue
        );
        emailService.sendEmail(customerEmail, subject, content);
    }
}