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
    private Date orderDate;

    private double totalAmount;

    private boolean confirmed;

    // Mối quan hệ với Customer (nhiều Order thuộc về một Customer)
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Mối quan hệ với Staff (nếu được xác nhận bởi nhân viên)
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    // Mối quan hệ với Manager (nếu cần xác nhận cấp cao)
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;
}
