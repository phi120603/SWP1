package com.example.swp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueRequest {
    @NotBlank(message = "Chủ đề không được để trống")
    @Size(max = 100, message = "Chủ đề tối đa 100 ký tự")
    private String subject;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    private Integer customerId;        // Wrapper type
   // private Integer assignedStaffId;   // Wrapper type
}
