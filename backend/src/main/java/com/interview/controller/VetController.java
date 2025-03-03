package com.interview.controller;

import com.interview.dto.CreateVetDTO;
import com.interview.dto.VetDTO;
import com.interview.service.VetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/vets")
@RequiredArgsConstructor
@Tag(name = "Veterinarian Management", description = "Operations pertaining to veterinarians in the shelter")
public class VetController {
    @NonNull
    private final VetService vetService;

    @Operation(summary = "Create a new veterinarian", description = "Creates a new veterinarian in the shelter system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Veterinarian created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<VetDTO> createVet(
            @Parameter(description = "Veterinarian to create", required = true)
            @Valid @RequestBody CreateVetDTO vetDTO) {
        log.debug("REST request to create Vet: {}", vetDTO);
        VetDTO result = vetService.create(vetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get a veterinarian by ID", description = "Returns a single veterinarian")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the veterinarian"),
        @ApiResponse(responseCode = "404", description = "Veterinarian not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VetDTO> getVet(
            @Parameter(description = "ID of veterinarian to be obtained", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get Vet: {}", id);
        VetDTO vetDTO = vetService.findById(id);
        return ResponseEntity.ok(vetDTO);
    }

    @Operation(
        summary = "Get all veterinarians",
        description = "Returns all veterinarians in the system with pagination. " +
                     "Use page and size parameters for pagination. " +
                     "Use sortBy and sortDirection for sorting (e.g., sortBy=name&sortDirection=ASC)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved veterinarians")
    })
    @GetMapping
    public ResponseEntity<Page<VetDTO>> getAllVets(
            @Parameter(
                description = "Page number (0-based)",
                schema = @Schema(type = "integer", defaultValue = "0")
            )
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                description = "Size of page",
                schema = @Schema(type = "integer", defaultValue = "10")
            )
            @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(
                description = "Property to sort by (e.g., name, specialization)",
                schema = @Schema(type = "string")
            )
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @Parameter(
                description = "Sort direction (ASC or DESC)",
                schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
            )
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        log.debug("REST request to get all Vets with pagination: page={}, size={}, sortBy={}, sortDirection={}", 
            page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VetDTO> result = vetService.findAll(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update an existing veterinarian", description = "Updates a veterinarian's information in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Veterinarian updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Veterinarian not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VetDTO> updateVet(
            @Parameter(description = "ID of veterinarian to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated veterinarian details", required = true)
            @Valid @RequestBody VetDTO vetDTO) {
        log.debug("REST request to update Vet: {}", id);
        VetDTO result = vetService.update(id, vetDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete a veterinarian", description = "Removes a veterinarian from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Veterinarian deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Veterinarian not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVet(
            @Parameter(description = "ID of veterinarian to delete", required = true)
            @PathVariable Long id) {
        log.debug("REST request to delete Vet: {}", id);
        vetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}