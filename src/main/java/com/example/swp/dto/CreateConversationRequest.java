package com.example.swp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {

    @NotNull(message = "Manager ID is required")
    private Integer managerId;

    @NotBlank(message = "Initial message cannot be empty")
    private String initialMessage;
}
