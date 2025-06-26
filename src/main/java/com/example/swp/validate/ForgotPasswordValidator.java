package com.example.swp.validate;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordValidator {
    public static Map<String, String> validate(String fullname, String idCitizen) {
        Map<String, String> errors = new HashMap<>();
        if (fullname == null || fullname.trim().isEmpty()) {
            errors.put("fullname", "Họ và tên là bắt buộc");
        }
        if (idCitizen == null || idCitizen.trim().isEmpty()) {
            errors.put("id_citizen", "Số CCCD/Hộ chiếu là bắt buộc");
        }
        return errors;
    }
}
