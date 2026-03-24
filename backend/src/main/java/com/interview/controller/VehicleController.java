package com.interview.controller;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.dto.VehicleSearchCriteria;
import com.interview.entity.Vehicle;
import com.interview.mapper.VehicleMapper;
import com.interview.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Create, search, update, and delete vehicle records")
public class VehicleController {
    private static final int MAX_PAGE_SIZE = 50;

    private final VehicleService vehicleService;

    @GetMapping
    @ResponseBody
    @Operation(summary = "List vehicles", description = "Returns a pageable list of vehicles filtered by optional search criteria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values or page size")
    })
    public Page<VehicleResponse> getVehicles(
            @Valid @ModelAttribute @ParameterObject VehicleSearchCriteria criteria,
            @ParameterObject Pageable pageable
    ) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not exceed " + MAX_PAGE_SIZE);
        }

        return vehicleService.findAll(criteria, pageable)
                .map(VehicleMapper::toResponse);
    }

    @GetMapping("/{id}")
    @ResponseBody
    @Operation(summary = "Get a vehicle", description = "Returns a single vehicle by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle returned"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public VehicleResponse getVehicle(@Parameter(description = "Vehicle identifier", example = "1") @PathVariable Long id) {
        return VehicleMapper.toResponse(vehicleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a vehicle", description = "Creates a new vehicle record from the provided request payload.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public VehicleResponse createVehicle(@Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleService.create(vehicle));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vehicle", description = "Replaces the mutable fields of an existing vehicle.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public VehicleResponse updateVehicle(
            @Parameter(description = "Vehicle identifier", example = "1") @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request
    ) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleService.update(id, vehicle));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle", description = "Deletes an existing vehicle by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle deleted"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public void deleteVehicle(@Parameter(description = "Vehicle identifier", example = "1") @PathVariable Long id) {
        vehicleService.delete(id);
    }
}