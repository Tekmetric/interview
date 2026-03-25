package com.interview.dto.request;

import com.interview.model.Mechanic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;

public record MechanicCreationRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50)
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        String lastName,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number")
        String phoneNumber,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotNull(message = "Mechanic shop ID is required")
        Long mechanicShopId,
        @NotNull(message = "Role is required")
        Mechanic.Role role) {
}
