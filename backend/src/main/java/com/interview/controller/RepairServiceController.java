package com.interview.controller;

import com.interview.dto.RepairServiceDTO;
import com.interview.dto.ApiResponseDTO;
import com.interview.service.RepairServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/repair-services")
@RequiredArgsConstructor
@Tag(name = "Repair Services", description = "API endpoints for managing repair services")
public class RepairServiceController {

    private final RepairServiceService repairServiceService;

    @Operation(summary = "Get a repair service by ID", description = "Retrieves a specific repair service by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the repair service",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RepairServiceDTO.class))),
        @ApiResponse(responseCode = "404", description = "Repair service not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required")
    })
    @PreAuthorize("hasRole('read') || hasRole('write')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> getRepairServiceById(
            @Parameter(description = "ID of the repair service to retrieve") @PathVariable Long id) {
        var repairService = repairServiceService.getRepairServiceById(id);

        var response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(repairService)
                .message("Repair service retrieved successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all repair services", description = "Retrieves a paginated list of repair services with sorting options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of repair services"),
        @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required")
    })
    @PreAuthorize("hasRole('read') || hasRole('write')")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<RepairServiceDTO>>> getAllRepairServices(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String sortDirection) {

        var sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        var pageable = PageRequest.of(pageNumber, pageSize, sort);
        var repairServicePage = repairServiceService.getAllRepairServices(pageable);

        var response = ApiResponseDTO.<Page<RepairServiceDTO>>builder()
                .success(true)
                .data(repairServicePage)
                .message("Repair services retrieved successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new repair service", description = "Creates a new repair service with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Repair service successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required")
    })
    @PreAuthorize("hasRole('write')")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> createRepairService(
            @Parameter(description = "Repair service details") @Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        var createdService = repairServiceService.createRepairService(repairServiceDTO);

        var response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(createdService)
                .message("Repair service created successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a repair service", description = "Updates an existing repair service with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Repair service successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Repair service not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required")
    })
    @PreAuthorize("hasRole('write')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> updateRepairService(
            @Parameter(description = "ID of the repair service to update") @PathVariable Long id,
            @Parameter(description = "Updated repair service details") @Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        var updatedService = repairServiceService.updateRepairService(id, repairServiceDTO);

        var response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(updatedService)
                .message("Repair service updated successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Delete a repair service", description = "Deletes a repair service by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Repair service successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Repair service not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required")
    })
    @PreAuthorize("hasRole('write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRepairService(
            @Parameter(description = "ID of the repair service to delete") @PathVariable Long id) {
        repairServiceService.deleteRepairService(id);
        
        var response = ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Repair service deleted successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }
}
