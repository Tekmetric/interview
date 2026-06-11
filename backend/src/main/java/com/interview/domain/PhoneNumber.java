package com.interview.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PhoneNumber(
        @JsonCreator
        @NotBlank(message = "Phone number must not be blank")
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        @Pattern(regexp = "^\\+?[0-9\\-\\s()]{7,20}$", message = "Phone number contains invalid characters")
        @JsonValue
        String phoneNumberString) {}
