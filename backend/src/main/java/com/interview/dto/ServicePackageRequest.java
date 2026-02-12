package com.interview.dto;

import com.interview.validation.SafeText;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ServicePackageRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @SafeText(type = SafeText.TextType.GENERAL, message = "Name contains potentially unsafe characters")
    String name,

    @SafeText(type = SafeText.TextType.GENERAL, message = "Description contains potentially unsafe characters")
    String description,

    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.01", message = "Monthly price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Monthly price must be a valid monetary amount")
    BigDecimal monthlyPrice
) {}