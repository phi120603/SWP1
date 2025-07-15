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
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            mailSender.send(message);
            log.info("Đã gửi mail test tới {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi gửi email tới {}: {}", to, e.getMessage(), e);
        }
    }

    @Override
    public void sendPaymentEmail(String to, Integer contractId, String pdfPath) {
        String subject = "Xác nhận thanh toán hợp đồng #" + contractId;
        String htmlBody = generateHtmlEmailBody(contractId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, pdfPath != null);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (pdfPath != null) {
                File file = new File(pdfPath);
                if (file.exists()) {
                    FileSystemResource resource = new FileSystemResource(file);
                    helper.addAttachment("hop_dong_" + contractId + ".pdf", resource);
                } else {
                    log.warn("Không tìm thấy file PDF: {}", pdfPath);
                }
            }

            mailSender.send(message);
            log.info("Đã gửi email hợp đồng đến {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi gửi email thanh toán: {}", e.getMessage(), e);
        }
    }

    private String generateHtmlEmailBody(Integer contractId) {
        return """
            <html>
              <body style="font-family: Arial, sans-serif; background-color: #f7f7f7; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: #ffffff; padding: 30px; border-radius: 8px;">
                  <h2 style="color: #0a5bd3;">✅ Hợp đồng #%d đã được ký</h2>
                  <p>Chúng tôi xác nhận bạn đã hoàn tất việc ký hợp đồng.</p>
                  <p>Vui lòng đọc kỹ các điều khoản dưới đây trước khi thanh toán:</p>
                  <ul>
                    <li>Khách hàng chịu trách nhiệm về hàng hóa lưu trữ.</li>
                    <li>Chi phí thuê kho áp dụng theo thời hạn hợp đồng.</li>
                    <li>Không hoàn tiền nếu đơn phương chấm dứt trước thời hạn.</li>
                    <li>Yêu cầu thanh toán trong vòng 3 ngày sau khi ký.</li>
                  </ul>
                  <a href="http://yourdomain.com/payment?contractId=%d"
                     style="display: inline-block; background-color: #0a5bd3; color: white;
                            padding: 12px 24px; text-decoration: none; border-radius: 4px;">
                     Tôi đồng ý & Thanh toán
                  </a>
                  <p style="margin-top: 30px;">Trân trọng,<br/>Đội ngũ Hợp đồng Điện tử</p>
                </div>
              </body>
            </html>
            """.formatted(contractId, contractId);
    }
}
