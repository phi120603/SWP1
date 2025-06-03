package com.example.swp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders") // Đặt tên khác vì "Order" là từ khóa SQL
public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(nullable = false)
        private Date startDate;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(nullable = false)
        private Date endDate;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(nullable = false)
        private Date orderDate;

        private double totalAmount;

        @Column(nullable = false)
        private String status; // PENDING, APPROVED, REJECTED, PAID

        @ManyToOne
        @JoinColumn(name = "customer_id", nullable = false)
        private Customer customer;

        @ManyToOne
        @JoinColumn(name = "staff_id")
        private Staff staff;

        @ManyToOne
        @JoinColumn(name = "manager_id")
        private Manager manager;

        @ManyToOne
        @JoinColumn(name = "storage_id", nullable = false)
        private Storage storage;

    }


