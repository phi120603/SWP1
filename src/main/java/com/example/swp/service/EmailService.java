package com.example.swp.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

//    void sendEmailWithQR(String to, String subject, String body, String qrContent);
}

