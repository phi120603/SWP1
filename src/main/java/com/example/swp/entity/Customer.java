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

    // Các quan hệ khác...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleName == null) {
            System.err.println("roleName is null for user: " + this.getUsername());
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName.name()));
    }
    public Customer(Integer id) {
        this.id = id;
    }

    @Override
    public String getPassword() { return this.password; }

    @Override
    public String getUsername() { return this.email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
