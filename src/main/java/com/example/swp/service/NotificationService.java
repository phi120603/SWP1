package com.example.swp.service;

import com.example.swp.entity.Notification;
import com.example.swp.entity.Customer;

import java.util.List;

public interface NotificationService {

    // Lấy danh sách thông báo của một khách hàng
    List<Notification> getNotificationsForCustomer(Customer customer);

    // Đếm số lượng thông báo chưa đọc
    long countUnreadNotifications(Customer customer);

    // Đánh dấu 1 thông báo là đã đọc
    void markAsRead(Long id);

    // Đánh dấu toàn bộ thông báo là đã đọc
    void markAllAsRead(Customer customer);

    // Tạo thông báo cho khách hàng
    void createNotification(String message, Customer customer);

    // Gửi thông báo cho khách hàng bằng ID
    void notifyCustomer(Integer customerId, String message);
}
