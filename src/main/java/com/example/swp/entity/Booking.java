package com.example.swp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class Booking {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private Date bookingDate;
        private String purpose;
        private int rentalDays;
        private double totalAmount;

        @ManyToOne
        @JoinColumn(name = "customer_id")
        private Customer customer;

        @ManyToOne
        @JoinColumn(name = "staff_id")
        private Staff staff;

        @ManyToOne
        @JoinColumn(name = "storage_id")
        private Storage storage;
    }


