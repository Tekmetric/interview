package com.interview.controller;

import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.VetDTO;
import com.interview.service.AnimalService;
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
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/animals")
@RequiredArgsConstructor
@Tag(name = "Animal Management", description = "Operations pertaining to animals in the shelter")
public class AnimalController {
    @NonNull
    private final AnimalService animalService;

    @Operation(summary = "Create a new animal", description = "Creates a new animal in the shelter system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Animal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<AnimalDTO> createAnimal(
            @Parameter(description = "Animal to create", required = true)
            @Valid @RequestBody CreateAnimalDTO animalDTO) {
        log.debug("REST request to create Animal: {}", animalDTO);
        final AnimalDTO result = animalService.create(animalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get an animal by ID", description = "Returns a single animal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the animal"),
        @ApiResponse(responseCode = "404", description = "Animal not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> getAnimal(
            @Parameter(description = "ID of animal to be obtained", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get Animal: {}", id);
        final AnimalDTO animalDTO = animalService.findById(id);
        return ResponseEntity.ok(animalDTO);
    }

    @Operation(summary = "Update an existing animal", description = "Updates an animal's information in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Animal updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Animal not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AnimalDTO> updateAnimal(
            @Parameter(description = "ID of animal to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated animal details", required = true)
            @Valid @RequestBody AnimalDTO animalDTO) {
        log.debug("REST request to update Animal: {}", id);
        final AnimalDTO result = animalService.update(id, animalDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete an animal", description = "Removes an animal from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Animal deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Animal not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(
            @Parameter(description = "ID of animal to delete", required = true)
            @PathVariable Long id) {
        log.debug("REST request to delete Animal: {}", id);
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get all animals",
        description = "Returns all animals with optional filtering and pagination. " +
                     "Use page and size parameters for pagination. " +
                     "Use sortBy and sortDirection for sorting (e.g., sortBy=name&sortDirection=ASC). " +
                     "All filters (name, age range, employee) can be combined."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved animals")
    })
    @GetMapping
    public ResponseEntity<Page<AnimalDTO>> getAllAnimals(
            @Parameter(description = "Filter by animal name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Minimum age in years")
            @RequestParam(required = false) Integer minAge,
            @Parameter(description = "Maximum age in years")
            @RequestParam(required = false) Integer maxAge,
            @Parameter(description = "Filter by responsible employee ID")
            @RequestParam(required = false) Long employeeId,
            @Parameter(
                description = "Page number (0-based)",
                schema = @Schema(type = "integer", defaultValue = "0")
            )
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                description = "Size of page",
                schema = @Schema(type = "integer", defaultValue = "20")
            )
            @RequestParam(required = false, defaultValue = "20") int size,
            @Parameter(
                description = "Property to sort by (e.g., name, dateOfBirth)",
                schema = @Schema(type = "string")
            )
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @Parameter(
                description = "Sort direction (ASC or DESC)",
                schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
            )
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        log.debug("REST request to get Animals with filters: name={}, minAge={}, maxAge={}, employeeId={}, page={}, size={}, sortBy={}, sortDirection={}", 
            name, minAge, maxAge, employeeId, page, size, sortBy, sortDirection);

        final Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);

        // Convert age filters to date range
        // startDate = oldest possible birthdate (from maxAge)
        // endDate = most recent possible birthdate (from minAge)
        final LocalDate startDate = maxAge != null ? LocalDate.now().minusYears(maxAge) : null;
        final LocalDate endDate = minAge != null ? LocalDate.now().minusYears(minAge) : null;

        return ResponseEntity.ok(animalService.findByFilters(
            name != null ? name.trim() : null,
            startDate,
            endDate,
            employeeId,
            pageable
        ));
    }

    @Operation(
        summary = "Get vets for an animal",
        description = "Returns all veterinarians assigned to a specific animal with pagination. " +
                     "Use page and size parameters for pagination. " +
                     "Use sortBy and sortDirection for sorting (e.g., sortBy=name&sortDirection=ASC)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved vets"),
        @ApiResponse(responseCode = "404", description = "Animal not found")
    })
    @GetMapping("/{id}/vets")
    public ResponseEntity<Page<VetDTO>> getAnimalVets(
            @Parameter(description = "ID of animal to get vets for", required = true)
            @PathVariable Long id,
            @Parameter(
                description = "Page number (0-based)",
                schema = @Schema(type = "integer", defaultValue = "0")
            )
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                description = "Size of page",
                schema = @Schema(type = "integer", defaultValue = "20")
            )
            @RequestParam(required = false, defaultValue = "20") int size,
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
        log.debug("REST request to get Vets for Animal: {}, page={}, size={}, sortBy={}, sortDirection={}", 
            id, page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
            
        return ResponseEntity.ok(animalService.findVetsByAnimalId(id, pageable));
    }
}