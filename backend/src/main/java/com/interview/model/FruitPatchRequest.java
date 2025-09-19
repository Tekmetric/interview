package com.interview.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FruitPatchRequest {

    @Size(max = 50)
    private String name;
    @Size(max = 50)
    private String color;
    @Size(max = 100)
    private String originCountry;
    @Size(max = 100)
    private String category;
    private Boolean organic;
    @Size(max = 50)
    private String batchNumber;
    @Size(max = 100)
    private String supplier;
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
    private Instant registrationDate;
}