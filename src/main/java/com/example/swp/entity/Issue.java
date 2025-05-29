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
    public class Issue {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String subject;
        private String description;
        private Date createdDate;
        private boolean resolved;

        @ManyToOne
        @JoinColumn(name = "customer_id")
        private Customer customer;

        @ManyToOne
        @JoinColumn(name = "staff_id")
        private Staff assignedStaff;
    }



