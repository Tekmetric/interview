package com.interview.controller;

import com.interview.common.dto.FlightDto;
import com.interview.common.dto.FlightRequestDto;
import com.interview.jpa.entity.enums.FlightEnum;
import com.interview.service.flight.FlightFetchService;
import com.interview.service.flight.FlightManagementService;
import com.interview.service.flight.model.FlightSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Flight", description = "Flight endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/flight", name = "Flight")
public class FlightController {

    private final FlightFetchService flightFetchService;
    private final FlightManagementService flightManagementService;

    @Operation(
            summary = "Search / list flights",
            description = "All filters optional: departureAirport, arrivalAirport, status, planeId, depFrom, depTo. " +
                    "Pagination via page,size,sort (optional)."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String departureAirport,
            @RequestParam(required = false) String arrivalAirport,
            @RequestParam(required = false) FlightEnum.Status status,
            @RequestParam(required = false) Integer planeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime depFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime depTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "departureTime,asc") String sort
    ) {
        FlightSearchCriteria criteria = FlightSearchCriteria.builder()
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .status(status)
                .planeId(planeId)
                .depFrom(depFrom)
                .depTo(depTo)
                .build();

        boolean paginate = (page != null && size != null);
        if (paginate) {
            Page<FlightDto> result = flightFetchService.search(criteria, page, size, sort);
            return ResponseEntity.ok(result);
        } else {
            List<FlightDto> result = flightFetchService.search(criteria);
            return ResponseEntity.ok(result);
        }
    }

    @Operation(summary = "Get flight by ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    @GetMapping(path = "{flightId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FlightDto> getFlightDTO(
            @Parameter(description = "Flight ID") @PathVariable("flightId") int flightId
    ) {
        FlightDto flightDto = flightFetchService.get(flightId);
        return ResponseEntity.ok(flightDto);
    }

    @Operation(summary = "Create flight")
    @ApiResponse(responseCode = "200", description = "Created")
    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FlightDto> create(@RequestBody FlightRequestDto request) {
        FlightDto created = flightManagementService.create(request);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Update flight")
    @ApiResponse(responseCode = "200", description = "Updated")
    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Version conflict", content = @Content)
    @PutMapping(path = "{flightId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FlightDto update(
            @Parameter(description = "Flight ID") @PathVariable("flightId") int flightId,
            @Valid @RequestBody FlightRequestDto request
    ) {
        return flightManagementService.update(flightId, request);
    }

    @Operation(summary = "Delete flight")
    @ApiResponse(responseCode = "204", description = "Deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    @DeleteMapping(path = "{flightId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Flight ID") @PathVariable("flightId") int flightId
    ) {
        flightManagementService.delete(flightId);
        return ResponseEntity.noContent().build();
    }
}
