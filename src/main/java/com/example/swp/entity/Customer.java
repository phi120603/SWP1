package com.example.swp.entity;

import com.example.swp.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fullname;
    private String address;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    private String id_citizen;
    private String password;

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

    //  Phân quyền động dựa trên roleName
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring yêu cầu ROLE_ prefix nếu dùng hasRole(...)
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName.name().toUpperCase()));
    }

    public Customer(Integer id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
