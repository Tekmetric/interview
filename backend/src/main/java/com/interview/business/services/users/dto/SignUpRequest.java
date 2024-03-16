package com.interview.business.services.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotNull
        @Size(min = 1)
        String name,

        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 6)
        String password,

        @NotNull
        @Size(min = 1)
        String avatar
) {

}