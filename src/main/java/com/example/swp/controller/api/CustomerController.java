package com.example.swp.controller.api;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.security.MyUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers(Authentication authentication) {
        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        // Only managers can access customer list
        if (!(userDetail.getUser() instanceof Manager)) {
            return ResponseEntity.status(403).build();
        }

        List<Customer> customers = customerRepository.findAll();
        List<CustomerDto> customerDtos = customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customerDtos);
    }

    private CustomerDto convertToDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .fullname(customer.getFullname())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .isOnline(customer.getIsOnline() != null ? customer.getIsOnline() : false)
                .lastSeen(customer.getLastSeen())
                .build();
    }

    // DTO class
    public static class CustomerDto {
        public Integer id;
        public String fullname;
        public String email;
        public String phone;
        public boolean isOnline;
        public java.time.LocalDateTime lastSeen;

        public static CustomerDtoBuilder builder() {
            return new CustomerDtoBuilder();
        }

        public static class CustomerDtoBuilder {
            private Integer id;
            private String fullname;
            private String email;
            private String phone;
            private boolean isOnline;
            private java.time.LocalDateTime lastSeen;

            public CustomerDtoBuilder id(Integer id) {
                this.id = id;
                return this;
            }

            public CustomerDtoBuilder fullname(String fullname) {
                this.fullname = fullname;
                return this;
            }

            public CustomerDtoBuilder email(String email) {
                this.email = email;
                return this;
            }

            public CustomerDtoBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }

            public CustomerDtoBuilder isOnline(boolean isOnline) {
                this.isOnline = isOnline;
                return this;
            }

            public CustomerDtoBuilder lastSeen(java.time.LocalDateTime lastSeen) {
                this.lastSeen = lastSeen;
                return this;
            }

            public CustomerDto build() {
                CustomerDto dto = new CustomerDto();
                dto.id = this.id;
                dto.fullname = this.fullname;
                dto.email = this.email;
                dto.phone = this.phone;
                dto.isOnline = this.isOnline;
                dto.lastSeen = this.lastSeen;
                return dto;
            }
        }
    }
}
