package com.interview.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phoneNumber,

    @Size(max = 255, message = "Address must not exceed 255 characters")
    String address
) {

  // Factory method for creating with required fields only
  public static CustomerRequestDTO of(String name, String email) {
    return new CustomerRequestDTO(name, email, null, null);
  }

}
