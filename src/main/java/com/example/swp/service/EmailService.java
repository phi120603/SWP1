package com.example.swp.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendPaymentEmail(String to, Integer contractId, String pdfPath);}
