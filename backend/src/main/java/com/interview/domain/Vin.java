package com.interview.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Vin(
        @JsonCreator
        @NotBlank(message = "VIN must not be blank")
        @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
        @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN contains invalid characters")
        @JsonValue
        String vinString) {}
