package com.example.swp.service.impl;

import com.example.swp.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class EmailServiceImpl implements EmailService {
        @Autowired
        private JavaMailSender sendEmail;

        public void sendEmail(String to, String subject, String body) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("spring.mail.username"); // Email gửi đi
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            sendEmail.send(message);
        }

//    public void sendEmailWithQR(String to, String subject, String body, String qrContent) {
//        try {
//            // 1. Tạo QR Code dưới dạng ảnh byte[]
//            byte[] qrImage = generateQRCodeImage(qrContent, 300, 300);
//
//            // 2. Tạo email dạng MimeMessage
//            MimeMessage message = sendEmail.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart
//
//            helper.setFrom("shop@gmail.com");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true); // true = html
//
//            // 3. Gắn QR code đính kèm
//            InputStreamSource attachmentSource = new ByteArrayResource(qrImage);
//            helper.addAttachment("qr-code.png", attachmentSource);
//
//            sendEmail.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Gửi email thất bại", e);
//        }
//    }
//
//    private byte[] generateQRCodeImage(String content, int width, int height) {
//        try {
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
//            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//            return pngOutputStream.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException("Tạo QR thất bại", e);
//        }
//    }
//
//    public void generateQRCode(String data, String path) throws Exception {
//        int width = 300;
//        int height = 300;
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
//        Path outputPath = Paths.get(path);
//        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", outputPath);
//    }
    }


