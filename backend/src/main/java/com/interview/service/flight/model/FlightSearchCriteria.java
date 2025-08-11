package com.interview.service.flight.model;

import com.interview.jpa.entity.enums.FlightEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Holder for flight search filters. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchCriteria {
    private String departureAirport;
    private String arrivalAirport;
    private FlightEnum.Status status;
    private Integer planeId;
    private LocalDateTime depFrom;
    private LocalDateTime depTo;
}
