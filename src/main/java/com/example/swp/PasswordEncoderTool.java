package com.example.swp;



import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.entity.Staff;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.ManageRepository;
import com.example.swp.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderTool implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ManageRepository manageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Mã hóa password cho Customer
        customerRepository.findAll().forEach(c -> {
            if (!c.getPassword().startsWith("$2a$")) { // kiểm tra chưa mã hóa BCrypt
                c.setPassword(passwordEncoder.encode(c.getPassword()));
                customerRepository.save(c);
            }
        });

        // Mã hóa password cho Staff
        staffRepository.findAll().forEach(s -> {
            if (!s.getPassword().startsWith("$2a$")) {
                s.setPassword(passwordEncoder.encode(s.getPassword()));
                staffRepository.save(s);
            }
        });

        // Mã hóa password cho Manager
        manageRepository.findAll().forEach(m -> {
            if (!m.getPassword().startsWith("$2a$")) {
                m.setPassword(passwordEncoder.encode(m.getPassword()));
                manageRepository.save(m);
            }
        });

        System.out.println(" Đã mã hóa mật khẩu thành công!");
    }
}

