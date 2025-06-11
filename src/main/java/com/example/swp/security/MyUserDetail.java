package com.example.swp.security;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.entity.Staff;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Getter
@Setter
public class MyUserDetail implements UserDetails {

    private UserDetails user;

    public MyUserDetail(Customer customer) {
        this.user = customer;
    }

    public MyUserDetail(Staff staff) {
        this.user = staff;
    }

    public MyUserDetail(Manager manager) {
        this.user = manager;
    }

    public MyUserDetail() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user instanceof Customer) {
            authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
        } else if (user instanceof Staff) {
            authorities.add(new SimpleGrantedAuthority("STAFF"));
        } else if (user instanceof Manager) {
            authorities.add(new SimpleGrantedAuthority("MANAGER"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // hoặc tùy logic của bạn
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
