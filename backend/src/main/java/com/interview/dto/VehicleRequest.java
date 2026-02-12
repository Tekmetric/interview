package com.interview.dto;

import com.interview.validation.CurrentYearOrPast;
import com.interview.validation.SafeText;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
    @NotNull(message = "Customer ID is required")
    Long customerId,

    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must contain only valid characters (no I, O, Q)")
    @SafeText(type = SafeText.TextType.GENERAL, message = "VIN contains potentially unsafe characters")
    String vin,

    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    @SafeText(type = SafeText.TextType.GENERAL, message = "Make contains potentially unsafe characters")
    String make,

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    @SafeText(type = SafeText.TextType.GENERAL, message = "Model contains potentially unsafe characters")
    String model,

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @CurrentYearOrPast(allowFutureYears = 1, message = "Year cannot be more than 1 year in the future")
    Integer year
) {}