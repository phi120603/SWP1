package com.example.swp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class EContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private LocalDate createdDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean signed;

    @Lob
    private String terms;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
