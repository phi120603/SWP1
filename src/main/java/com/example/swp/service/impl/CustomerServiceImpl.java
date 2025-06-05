//package com.example.swp.service.impl;
//
//import com.example.swp.dto.LoginRequest;
//import com.example.swp.dto.LoginResponse;
//import com.example.swp.dto.RegisterRequest;
//import com.example.swp.entity.Customer; // Đảm bảo entity Customer của bạn tồn tại
//import com.example.swp.repository.CustomerRepository; // Đảm bảo CustomerRepository của bạn tồn tại
//import com.example.swp.service.CustomerService; // Interface CustomerService
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class CustomerServiceImpl implements CustomerService { // Đảm bảo implements interface
//@Autowired
//    private  CustomerRepository customerRepository;
////    private final PasswordEncoder passwordEncoder; // Đây là nơi IDE báo lỗi autowire trước đó
//
////    @Autowired // Spring sẽ tự động inject CustomerRepository và PasswordEncoder
////    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
////        this.customerRepository = customerRepository;
////        this.passwordEncoder = passwordEncoder;
////    }
//
//    @Override // Triển khai phương thức từ CustomerService interface
//    public Customer register(RegisterRequest request) {
//        // Kiểm tra username đã tồn tại trong DB
//        if (customerRepository.findByUsername(request.getUsername()).isPresent()) {
//            throw new IllegalArgumentException("Username '" + request.getUsername() + "' already exists!");
//        }
//
//        // Kiểm tra email đã tồn tại trong DB (nếu email không rỗng)
//        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) { // Thêm kiểm tra rỗng
//            if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
//                throw new IllegalArgumentException("Email '" + request.getEmail() + "' already exists!");
//            }
//        } else {
//            throw new IllegalArgumentException("Email cannot be empty."); // Yêu cầu email không rỗng
//        }
//
//
//        // Tạo một đối tượng Customer mới
//        Customer newCustomer = new Customer();
//        newCustomer.setUsername(request.getUsername());
//        newCustomer.setEmail(request.getEmail());
//
//        // MÃ HÓA MẬT KHẨU TRƯỚC KHI LƯU VÀO DATABASE
//        newCustomer.setPassword(passwordEncoder.encode(request.getPassword())); // Lỗi "cannot be applied to String" sẽ biến mất sau khi sửa
//
//        // TODO: Thiết lập các trường khác nếu có (ví dụ: role, creation date, v.v.)
//        // newCustomer.setRole("USER");
//
//        return customerRepository.save(newCustomer); // Lưu Customer vào database
//    }
//
//    @Override // Triển khai phương thức từ CustomerService interface
//    public LoginResponse login(LoginRequest request) {
//        // Tìm Customer theo username
//        Optional<Customer> customerOptional = customerRepository.findByUsername(request.getUsername());
//
//        // Nếu không tìm thấy hoặc mật khẩu không khớp
//        if (customerOptional.isEmpty() || !passwordEncoder.matches(request.getPassword(), customerOptional.get().getPassword())) {
//            throw new IllegalArgumentException("Invalid username or password.");
//        }
//
//        Customer customer = customerOptional.get();
//
//        // Tạo LoginResponse
//        LoginResponse response = new LoginResponse();
//        response.setUsername(customer.getUsername());
//        response.setCustomerId(String.valueOf(customer.getId())); // Đảm bảo entity Customer có trường ID
//
//        // TODO: Nếu bạn đang dùng JWT, tạo và đặt JWT token vào đây:
//        // response.setAccessToken(jwtTokenProvider.generateToken(customer));
//
//        return response;
//    }
//}