package com.interview.dto;

import com.interview.enums.ContactMethod;
import com.interview.validation.SafeText;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CustomerRequest(
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @SafeText(type = SafeText.TextType.NAME, message = "First name contains invalid characters")
    String firstName,

    @Min(value = 0, message = "Version must be non-negative")
    Long version,

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @SafeText(type = SafeText.TextType.NAME, message = "Last name contains invalid characters")
    String lastName,

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @SafeText(type = SafeText.TextType.EMAIL, message = "Email contains potentially unsafe characters")
    String email,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @SafeText(type = SafeText.TextType.PHONE, message = "Phone number contains invalid characters")
    String phone,

    // Profile fields
    @SafeText(type = SafeText.TextType.ADDRESS, message = "Address contains potentially unsafe characters")
    String address,

    @PastOrPresent(message = "Date of birth cannot be in the future")
    LocalDate dateOfBirth,

    ContactMethod preferredContactMethod
) {}