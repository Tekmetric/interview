package com.interview.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MechanicShopCreationRequest(
        @NotBlank(message = "Shop name is required")
        @Size(max = 100)
        String shopName,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number")
        String phoneNumber,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email) {
}
