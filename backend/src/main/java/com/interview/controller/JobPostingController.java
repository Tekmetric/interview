package com.interview.controller;

import com.interview.dto.JobPostingFilter;
import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/job-postings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Job Postings", description = "Create, retrieve, update, delete and filter job vacancies")
public class JobPostingController {

    private final JobPostingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a job posting", description = "Creates a new job posting. Status defaults to DRAFT when omitted.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = JobPostingResponse.class))), @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),})
    public JobPostingResponse create(@Valid @RequestBody JobPostingRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job posting by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Found", content = @Content(schema = @Schema(implementation = JobPostingResponse.class))), @ApiResponse(responseCode = "404", description = "Not found", content = @Content),})
    public ResponseEntity<JobPostingResponse> findById(@Parameter(description = "Job posting ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "List job postings", description = """
            Returns a paginated list of job postings.
            
            **Filtering** (all params are optional and combinable):
            - `remote=true|false` — filter by remote flag
            - `location=Houston` — case-insensitive substring match on location
            - `titleContains=Engineer` — case-insensitive substring match on title
            
            **Sorting** examples: `?sort=postedAt,desc` · `?sort=title,asc`
            """)
    @ApiResponse(responseCode = "200", description = "Paginated results")
    public Page<JobPostingResponse> findAll(@ParameterObject JobPostingFilter filter, @PageableDefault(size = 20, sort = "id") @ParameterObject Pageable pageable) {
        return service.findAll(filter, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job posting", description = "Replaces all editable fields. CLOSED and ARCHIVED postings cannot be edited.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated"), @ApiResponse(responseCode = "400", description = "Validation error", content = @Content), @ApiResponse(responseCode = "404", description = "Not found", content = @Content), @ApiResponse(responseCode = "409", description = "Illegal state", content = @Content),})
    public ResponseEntity<JobPostingResponse> update(@PathVariable Long id, @Valid @RequestBody JobPostingRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a job posting")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted"), @ApiResponse(responseCode = "404", description = "Not found", content = @Content),})
    public void delete(@Parameter(description = "Job posting ID", example = "1") @PathVariable Long id) {
        service.delete(id);
    }
}
