package com.example.swp.controller.api;

import com.example.swp.service.impl.VnPayServiceimpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final VnPayServiceimpl vnPayService;

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(HttpServletRequest request,
                                           @RequestParam long amount) {
        try {
            String redirectUrl = vnPayService.createPaymentUrl(request, amount);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tạo thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/payment-return")
    public ResponseEntity<String> paymentReturn(HttpServletRequest request) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        if ("00".equals(vnp_ResponseCode)) {
            return ResponseEntity.ok("Thanh toán thành công!");
        } else {
            return ResponseEntity.ok("Thanh toán thất bại! Mã lỗi: " + vnp_ResponseCode);
        }
    }
}
