package com.interview.dto;

import com.interview.entity.Role;
import com.interview.validation.Lowercase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be no longer than 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Lowercase(message = "Email must be in lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters")
    private String password;

    // If role is not provided in the request, default as USER
    private Role role = Role.USER;

    // Optional addresses
    // @Valid: Validate nested objects (a customer has a list of nested addresses)
    @Valid
    private List<CreateAddressRequest> addresses;
}
