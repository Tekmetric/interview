package com.interview.models.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.models.user.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @Size(max = 50, message = "Middle name must be at most 50 characters")
        String middleName,

        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
        String username,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^_-]).{8,64}$",
                message = "Password must contain at least one uppercase, one lowercase, one number, and one special character"
        )
        String password,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Pattern(regexp = "^(?:\\d{9}|\\d{3}-\\d{2}-\\d{4})$", message = "SSN must be 9 digits, with or without dashes (###-##-####)")
        String ssn,

        Gender gender,

        @Email(message = "Invalid email address")
        String email,

        @Pattern(regexp = "^[0-9-]{10,20}$", message = "Phone number must be 10–15 digits and may include dashes")
        String phoneNumber
) {}

