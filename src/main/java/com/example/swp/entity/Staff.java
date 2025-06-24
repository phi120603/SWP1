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
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int staffid;
    private String fullname;
    private String email;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private RoleName roleName;
    private boolean sex;
    private String idCitizenCard;

    @OneToMany(mappedBy = "staff")
    private List<Contact> contacts;

}
