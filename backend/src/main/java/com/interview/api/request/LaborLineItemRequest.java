package com.interview.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LaborLineItemRequest(
        @NotBlank @Size(max = 255) String name,
        @Min(1) int quantity,
        @NotNull UUID serviceCode) {}
