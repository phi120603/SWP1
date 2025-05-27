package com.example.swp.dto;

import com.example.swp.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StaffRequest {
    private int staffid;
    private String fullname;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private boolean sex;
    private String idCitizenCard;
}
