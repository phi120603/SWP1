package com.example.swp.service.impl; // Đảm bảo đúng package của bạn

import com.example.swp.service.EmailService; // Import interface EmailService của bạn
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("shop@gmail.com");
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}


