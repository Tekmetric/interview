package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    public String oldPassword;

    @NotBlank(message = "New password is required")
    public String newPassword;
}
