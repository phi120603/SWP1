package com.example.swp.security;

import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.ManageRepository;
import com.example.swp.repository.StaffReponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffReponsitory staffRepository;

    @Autowired
    private ManageRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByEmail(username)
                .map(user -> (UserDetails) user)
                .or(() -> staffRepository.findByEmail(username).map(user -> (UserDetails) user))
                .or(() -> managerRepository.findByEmail(username).map(user -> (UserDetails) user))
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
    }
}
