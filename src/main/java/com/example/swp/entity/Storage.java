package com.example.swp.entity;

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
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int storageid;
    private String storagename;
    private String address;
    private String city;
    private String state;
    private boolean status;


    @OneToMany(mappedBy = "storage")
    private List<Contact> contacts;

    @OneToMany(mappedBy = "storage")
    private List<InventoryTransaction> inventoryTransactions;
}
