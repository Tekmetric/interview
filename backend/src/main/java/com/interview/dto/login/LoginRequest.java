package com.interview.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Size(max = 256)
        String username,
        @NotBlank
        @Size(max = 256)
        String password) {
}
