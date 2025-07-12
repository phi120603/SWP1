package com.example.swp.validate;

import com.example.swp.entity.Customer;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CustomerValidator {
    public static Map<String, String> validate(Customer customer) {
        Map<String, String> errors = new HashMap<>();

        if (customer == null) {
            errors.put("global", "Dữ liệu khách hàng không hợp lệ!");
            return errors;
        }

        if (!StringUtils.hasText(customer.getFullname()) || customer.getFullname().length() < 2)
            errors.put("fullname", "Tên khách hàng phải có ít nhất 2 ký tự!");

        if (!isValidEmail(customer.getEmail()))
            errors.put("email", "Email không hợp lệ!");

        if (!isValidPhone(customer.getPhone()))
            errors.put("phone", "Số điện thoại phải bắt đầu bằng 0 và có 10-11 số!");

        if (!StringUtils.hasText(customer.getAddress()))
            errors.put("address", "Địa chỉ không được để trống!");

        if (!StringUtils.hasText(customer.getId_citizen()) || customer.getId_citizen().length() != 12)
            errors.put("id_citizen", "Số CCCD phải có 12 số!");

        if (customer.getPassword() == null || customer.getPassword().length() < 8)
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự!");

        if (customer.getRoleName() == null)
            errors.put("roleName", "Vui lòng chọn vai trò!");

        return errors;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0\\d{9,10}$");
    }
}
