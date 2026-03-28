package com.interview.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FruitCreateRequest {

    @Size(max = 50)
    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 50)
    private String color;
    @Size(max = 100)
    private String originCountry;
    @Size(max = 100)
    private String category;

    private Boolean organic;

    @Size(max = 50)
    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @Size(max = 100)
    @NotBlank(message = "Supplier is required")
    private String supplier;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;

    private Instant registrationDate;
}
