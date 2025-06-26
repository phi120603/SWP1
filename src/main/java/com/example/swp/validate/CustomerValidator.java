package com.example.swp.validate;

import com.example.swp.entity.Customer;
import org.springframework.util.StringUtils;

public class CustomerValidator {
    public static String validate(Customer customer) {
        if (customer == null) return "Dữ liệu khách hàng không hợp lệ!";

        if (!StringUtils.hasText(customer.getFullname()) || customer.getFullname().length() < 2)
            return "Tên khách hàng phải có ít nhất 2 ký tự!";

        if (!isValidEmail(customer.getEmail()))
            return "Email không hợp lệ!";

        if (!isValidPhone(customer.getPhone()))
            return "Số điện thoại phải bắt đầu bằng 0 và có 10-11 số!";

        if (!StringUtils.hasText(customer.getAddress()))
            return "Địa chỉ không được để trống!";

        if (!StringUtils.hasText(customer.getId_citizen()) || customer.getId_citizen().length() != 9)
            return "Số CCCD phải 12 số!";

        // Kiểm tra password nếu là tạo mới (edit có thể cho phép để trống)
        if (customer.getPassword() != null && customer.getPassword().length() > 0 && customer.getPassword().length() < 6)
            return "Mật khẩu phải có ít nhất 6 ký tự!";

        return null; // Hợp lệ
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0\\d{9,10}$");
    }
}
