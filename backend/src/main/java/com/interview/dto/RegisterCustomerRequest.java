package com.interview.dto;

import com.interview.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// TODO EXPLAIN: Getters, Setters, toString(), equals(), hashCode(), A required-arguments constructor
@Data
public class RegisterCustomerRequest {
    private String firstName;

    // TODO EXPLAIN: validation
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be no longer than 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    // TODO EXPLAIN: MethodArgumentNotValidException, Spring MVC validates a controller method param DTO
    @Lowercase(message = "Email must be in lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters")
    private String password;
}
