package com.example.swp.service.impl;

import com.example.swp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("shop@gmail.com"); // Nên cấu hình trong application.properties
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendPaymentEmail(String customerEmail, Integer contractId) {
        String subject = "Thông báo thanh toán hợp đồng #" + contractId;
        String body = "Bạn vừa ký hợp đồng #" + contractId + ". Vui lòng truy cập hệ thống để tiến hành thanh toán.";
        sendEmail(customerEmail, subject, body); // ✅ gọi lại phương thức đã viết
    }
}
