package com.interview.controller;

import com.interview.dto.PageResponse;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.dto.VehicleSearchCriteria;
import com.interview.entity.Vehicle;
import com.interview.mapper.VehicleMapper;
import com.interview.security.AuthenticatedUser;
import com.interview.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Create, search, update, and delete vehicle records in the self-service portal")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {
    private static final int MAX_PAGE_SIZE = 50;

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "List vehicles", description = "Returns a pageable list of vehicles belonging to the authenticated vehicle owner, or all vehicles for admins, filtered by optional search criteria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values or page size"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public PageResponse<VehicleResponse> getVehicles(
            @Valid @ModelAttribute @ParameterObject VehicleSearchCriteria criteria,
            @ParameterObject Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not exceed " + MAX_PAGE_SIZE);
        }

        Page<VehicleResponse> vehiclePage = vehicleService.findAll(criteria, pageable, currentUser)
                .map(VehicleMapper::toResponse);

        return PageResponse.from(vehiclePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a vehicle", description = "Returns a single vehicle by its identifier if it belongs to the authenticated vehicle owner, or any vehicle for admins.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public VehicleResponse getVehicle(
            @Parameter(description = "Vehicle identifier", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return VehicleMapper.toResponse(vehicleService.findById(id, currentUser));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a vehicle", description = "Creates a new vehicle record owned by the authenticated vehicle owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public VehicleResponse createVehicle(
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleService.create(vehicle, currentUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vehicle", description = "Replaces the mutable fields of an existing vehicle owned by the authenticated vehicle owner, or any vehicle for admins.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public VehicleResponse updateVehicle(
            @Parameter(description = "Vehicle identifier", example = "1") @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleService.update(id, vehicle, currentUser));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vehicle", description = "Deletes an existing vehicle owned by the authenticated vehicle owner, or any vehicle for admins.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public void deleteVehicle(
            @Parameter(description = "Vehicle identifier", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        vehicleService.delete(id, currentUser);
    }
}


