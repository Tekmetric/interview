package com.interview.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MechanicShopUpdateRequest(
        @Size(max = 100)
        String shopName,
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number")
        String phoneNumber,
        @Email(message = "Invalid email format")
        String email) {
}
