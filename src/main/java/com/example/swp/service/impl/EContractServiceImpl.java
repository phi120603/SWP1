package com.example.swp.service.impl;

import com.example.swp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendPaymentEmail(String to, Integer contractId, String pdfPath) {
        String subject = "Xác nhận thanh toán hợp đồng #" + contractId;
        String body = "Cảm ơn bạn đã ký hợp đồng.\n"
                + "Vui lòng thanh toán tại: http://yourdomain.com/payment?contractId=" + contractId;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, pdfPath != null);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            if (pdfPath != null) {
                FileSystemResource file = new FileSystemResource(new File(pdfPath));
                helper.addAttachment("hop_dong_" + contractId + ".pdf", file);
            }

            mailSender.send(message);
            log.info("Email đã được gửi tới: {}", to);

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email tới {}: {}", to, e.getMessage(), e);
        }
    }
}
