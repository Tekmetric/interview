package com.interview.common.dto;

import com.interview.jpa.entity.enums.FlightEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read model returned by the API for a flight:
 * core fields + small plane summary + audit info.
 */
@Data
public class FlightDto {

    private Integer id;
    private Long version;

    private String code;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private FlightEnum.Status status;
    private Integer availableSeats;
    private BigDecimal price;
    private String currency;
    private String terminal;
    private String gate;

    private Integer planeId;
    private String planeRegistration;
    private String planeModel;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdByUsername;
    private String updatedByUsername;
}
