package com.example.swp.entity;

import com.example.swp.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String username;
    private int id;
    private String fullname;
    private String address;
    private String phone;
    private String email;
    @Enumerated(EnumType.STRING)
    private RoleName rolename;
    private String id_citizen;
    private String password;
    @Column(unique = true, nullable = false)

    @Enumerated(EnumType.STRING)


    @OneToMany(mappedBy = "customer")
    private List<Contact> contacts;

    @OneToMany(mappedBy = "customer")
    private List<Feedback> feedbacks;

    @OneToMany
    private List<StorageTransaction> storageTransactions;

    @OneToMany
    private List<Payment> payments;

    @OneToMany
    private List<WishList> wishLists;
}
