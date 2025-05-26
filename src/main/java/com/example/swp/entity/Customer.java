package com.example.swp.entity;

import com.example.swp.enums.Role;
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
    private int id;
    private String fullname;
    private String address;
    private String phone;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String id_citizen;

    // Contact
    //1-many
    // feedback
    //1-many
    //inventoryTransaction
    //1-many
    //payment
    //1-many
    //waitlist
    @OneToMany(mappedBy = "customer")
    private List<Contact> contacts;

    @OneToMany(mappedBy = "customer")
    private List<Feedback> feedbacks;

    @OneToMany
    private List<InventoryTransaction> inventoryTransactions;

    @OneToMany
    private List<Payment> payments;

    @OneToMany
    private List<WishList> wishLists;
}
