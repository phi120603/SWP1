package com.example.swp.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    default void sendOtpEmail(String to, String otp) {
        String subject = "OTP Check-in";
        String content = "Mã OTP check-in của bạn là: " + otp;
        sendEmail(to, subject, content);
    }
}

