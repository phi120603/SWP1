package com.example.swp.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String htmlBody);

//    void sendEmailWithQR(String to, String subject, String body, String qrContent);
}

