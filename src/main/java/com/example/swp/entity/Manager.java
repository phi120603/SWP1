package com.example.swp.entity;

import com.example.swp.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class Manager {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String fullname;
        private String email;
        private String password;
        private String phone;

        @Enumerated(EnumType.STRING)
        private RoleName roleName;

        @OneToMany(mappedBy = "manager")
        private List<Order> orders;
    }


