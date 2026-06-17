package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credentials used to obtain a JWT access token")
public record LoginRequest(
        @Schema(description = "Vehicle owner email address", example = "owner1@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Plaintext password", example = "password")
        @NotBlank
        String password
) {}


