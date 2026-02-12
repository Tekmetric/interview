package com.interview.controller;

import com.interview.dto.AthleteMapper;
import com.interview.dto.AthleteRequest;
import com.interview.dto.AthleteResponse;
import com.interview.dto.PagedResponse;
import com.interview.model.Athlete;
import com.interview.repository.AthleteSpecification;
import com.interview.service.AthleteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for athlete resources.
 */
@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
@Tag(name = "Athletes", description = "Athlete management API with filtering, pagination, and sorting")
public class AthleteController {

    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "firstName", "lastName", "nationality", "discipline");

    private final AthleteService service;

    @Operation(
            summary = "Get all athletes",
            description = "Retrieves a paginated list of athletes with optional filtering and sorting. "
                    + "Supports filtering by nationality, discipline, and name search.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved athletes",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    @GetMapping
    public ResponseEntity<PagedResponse<AthleteResponse>> getAllAthletes(
            @Parameter(description = "Filter by nationality (case-insensitive partial match)", example = "USA")
                    @RequestParam(required = false)
                    String nationality,
            @Parameter(description = "Filter by discipline (case-insensitive partial match)", example = "100m")
                    @RequestParam(required = false)
                    String discipline,
            @Parameter(description = "Search by first or last name (case-insensitive partial match)", example = "bolt")
                    @RequestParam(required = false)
                    String search,
            @Parameter(description = "Page number (0-based)", example = "0")
                    @RequestParam(defaultValue = "0")
                    @PositiveOrZero
                    int page,
            @Parameter(description = "Page size (1–100)", example = "10")
                    @RequestParam(defaultValue = "10")
                    @Positive
                    @Max(MAX_SIZE)
                    int size,
            @Parameter(description = "Sort field", example = "lastName")
                    @RequestParam(defaultValue = DEFAULT_SORT_FIELD)
                    String sortBy,
            @Parameter(description = "Sort direction", example = "ASC") @RequestParam(defaultValue = "ASC")
                    String direction) {

        final Specification<Athlete> spec = buildSpecification(nationality, discipline, search);

        // Validate sort field
        final String sortField = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : DEFAULT_SORT_FIELD;
        final Sort.Direction sortDirection =
                direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        final Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        final Page<Athlete> athletePage = service.findAll(spec, pageable);

        return ResponseEntity.ok(toPagedResponse(athletePage));
    }

    @Operation(summary = "Get athlete by ID", description = "Retrieves a specific athlete by their unique identifier")
    @ApiResponse(responseCode = "200", description = "Athlete found")
    @ApiResponse(responseCode = "404", description = "Athlete not found")
    @GetMapping("/{id}")
    public ResponseEntity<AthleteResponse> getAthleteById(@PathVariable Long id) {
        final Athlete athlete = service.findById(id); // May throw AthleteNotFoundException
        return ResponseEntity.ok(AthleteMapper.toResponse(athlete));
    }

    @Operation(summary = "Create a new athlete", description = "Creates a new athlete with the provided data")
    @ApiResponse(responseCode = "201", description = "Athlete created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid athlete data")
    @ApiResponse(responseCode = "409", description = "Athlete already exists")
    @PostMapping
    public ResponseEntity<AthleteResponse> createAthlete(@Valid @RequestBody final AthleteRequest request) {
        final Athlete athlete = AthleteMapper.toDomain(request);
        final Athlete saved = service.save(athlete);
        return ResponseEntity.status(HttpStatus.CREATED).body(AthleteMapper.toResponse(saved));
    }

    @Operation(summary = "Update an athlete", description = "Updates an existing athlete with the provided data")
    @ApiResponse(responseCode = "200", description = "Athlete updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid athlete data")
    @ApiResponse(responseCode = "404", description = "Athlete not found")
    @PutMapping("/{id}")
    public ResponseEntity<AthleteResponse> updateAthlete(
            @PathVariable Long id, @Valid @RequestBody AthleteRequest request) {
        final Athlete existing = service.findById(id); // May throw AthleteNotFoundException
        AthleteMapper.updateFromRequest(existing, request);
        final Athlete updated = service.save(existing);
        return ResponseEntity.ok(AthleteMapper.toResponse(updated));
    }

    @Operation(summary = "Delete an athlete", description = "Deletes an athlete by their unique identifier")
    @ApiResponse(responseCode = "204", description = "Athlete deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Private helper methods ---

    private Specification<Athlete> buildSpecification(
            final String nationality, final String discipline, final String search) {
        Specification<Athlete> spec = (root, query, cb) -> cb.conjunction();

        if (nationality != null && !nationality.isBlank()) {
            spec = spec.and(AthleteSpecification.hasNationality(nationality));
        }
        if (discipline != null && !discipline.isBlank()) {
            spec = spec.and(AthleteSpecification.hasDiscipline(discipline));
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and(AthleteSpecification.nameContains(search));
        }

        return spec;
    }

    private PagedResponse<AthleteResponse> toPagedResponse(final Page<Athlete> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(AthleteMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
