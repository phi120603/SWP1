//
//package com.example.swp.service.impl;
//
//import com.example.swp.dto.LoginRequest;
//import com.example.swp.entity.Customer;
//import com.example.swp.repository.LoginResponsitory;
//import com.example.swp.service.LoginService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class LoginServiceImpl implements LoginService {
//
//    @Autowired
//    private LoginResponsitory loginResponsitory;
//
//    public ResponseEntity<?> login(LoginRequest request) {
//        Optional<Customer> customerOpt = LoginService.findByEmail(request.getEmail());
//
//        if (customerOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Email không tồn tại");
//        }
//
//        Customer customer = customerOpt.get();
//
//        if (!customer.getPassword().equals(request.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Sai mật khẩu");
//        }
//
//        return ResponseEntity.ok("Đăng nhập thành công");
//    }
//
//    @Override
//    public Optional<Customer> findByEmail(String email) {
//        return loginResponsitory.findByEmail(email);
//    }
//}
//
