//package com.example.swp.controller.api;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//
//@RestController
//@RequestMapping("/api/messages")
//public class MessageController {
//
//    @Autowired
//    private MessageRepository messageRepository;
//
//    @PostMapping
//    public ResponseEntity<?> saveMessage(@RequestBody Message message) {
//        message.setTimestamp(LocalDateTime.now());
//        return ResponseEntity.ok(messageRepository.save(message));
//    }
//}
//
