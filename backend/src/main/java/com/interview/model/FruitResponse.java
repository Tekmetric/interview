package com.interview.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FruitResponse {

    private Long id;
    private String name;
    private String batchNumber;
    private String color;
    private String originCountry;
    private String category;
    private String supplier;
    private Boolean organic;
    private Integer quantity;
    private Instant registrationDate;
    private Instant lastUpdateDate;
}
