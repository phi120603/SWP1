package com.example.swp.security;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.entity.Staff;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyUserDetail implements UserDetails {
    private Customer customer;
    private Staff staff;
    private Manager manager;
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (customer != null) {
            authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
        } else if (staff != null) {
            authorities.add(new SimpleGrantedAuthority("STAFF"));
        } else if (manager != null) {
            authorities.add(new SimpleGrantedAuthority("MANAGER"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        if (customer != null) return customer.getPassword();
        if (staff != null) return staff.getPassword();
        if (manager != null) return manager.getPassword();
        return null;
    }

    @Override
    public String getUsername() {
        if (customer != null) return customer.getEmail();
        if (staff != null) return staff.getEmail();
        if (manager != null) return manager.getEmail();
        return null;
    }


}
