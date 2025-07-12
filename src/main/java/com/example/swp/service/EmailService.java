package com.example.swp.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendPaymentEmail(String customerEmail, Integer contractId); // ✅ thêm phương thức này
}
