package com.example.swp.controller.api;

import com.example.swp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/testMail")
public class Testmail {
    @Autowired
    private EmailService emailService;
    @PostMapping("/send_mail")
    public ResponseEntity<String> sendMail(){
        emailService.sendEmail("thangphan14122003@gmail.com", "laoas", "test");
        return ResponseEntity.ok("success");
    }
}
