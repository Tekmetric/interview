package com.interview.common.dto;

import com.interview.jpa.entity.enums.FlightEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request body for creating/updating a flight.
 * Field-level constraints via Jakarta annotations; cross-field/business rules validated separately.
 */
@Data
public class FlightRequestDto {

    @NotBlank
    @Size(max = 10)
    private String code;

    @NotBlank @Pattern(regexp = "^[A-Z]{3}$")
    private String departureAirport;

    @NotBlank @Pattern(regexp = "^[A-Z]{3}$")
    private String arrivalAirport;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private LocalDateTime arrivalTime;

    @NotNull
    private FlightEnum.Status status;

    @NotNull
    @PositiveOrZero
    private Integer availableSeats;

    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal price;

    @Pattern(regexp = "^[A-Z]{3}$")
    private String currency = "EUR";

    @Size(max = 5) private String terminal;
    @Size(max = 5) private String gate;

    @NotNull
    @Positive
    private Integer planeId;
}
