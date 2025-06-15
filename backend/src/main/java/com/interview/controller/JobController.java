package com.interview.controller;


import com.interview.model.JobStatus;
import com.interview.model.dto.JobCreateRequest;
import com.interview.model.dto.JobResponse;
import com.interview.model.dto.JobUpdateRequest;
import com.interview.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Jobs", description = "Operations related to jobs")
public class JobController {

    private final JobService jobService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get a job by ID",
            description = "Retrieves a specific job based on its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the job",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JobResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Job not found")
            })
    public JobResponse jobs(@Parameter(description = "ID of the job to retrieve", required = true) @PathVariable Integer id) {
        return jobService.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new job",
            description = "Creates a new job.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Job object to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobCreateRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the job",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JobResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Car with provided carId was not found")
            })
    public JobResponse createJob(@RequestBody JobCreateRequest jobCreateRequest) {
        return jobService.createJob(jobCreateRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing job",
            description = "Updates an existing job based on its ID.",
            parameters = @Parameter(description = "ID of the job to update", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Job object to be updated",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobUpdateRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the job"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Job not found")
            })
    public void updateJob(@PathVariable Integer id, @RequestBody JobUpdateRequest jobUpdateRequest) {
        jobService.updateJob(id, jobUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a job by ID",
            description = "Deletes a specific job and its tasks based on the  job ID.",
            parameters = @Parameter(description = "ID of the job to delete", required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the job"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Job not found")
            })
    public void deleteJob(@PathVariable Integer id) {
        jobService.deleteJob(id);
    }

    @GetMapping("/car/{vin}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Find all jobs by car VIN",
            description = "Retrieves all jobs associated with a specific car VIN.",
            parameters = @Parameter(description = "VIN of the car", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the jobs",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = JobResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires USER role"),
                    @ApiResponse(responseCode = "404", description = "No jobs found for the given VIN")
            })
    public List<JobResponse> findAllJobsByCar(@PathVariable String vin) {
        return jobService.findAllJobsByCar(vin);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Find all jobs with pagination and optional status filtering",
            description = "Retrieves a paginated list of all jobs, with optional filtering by status.",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name= "page", description = "Page number (default: 0)"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Size of the page (default: 20)"),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Sorting criteria in the format: property(,asc|desc)(,IgnoreCase). Default sort order is ascending. Multiple sort criteria are supported.", example = "id,DESC"),
                    @Parameter(in = ParameterIn.QUERY, name = "statuses", description = "List of job statuses to filter by (default: all statuses)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of jobs",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PagedModel.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
            })
    public PagedModel<JobResponse> findAllJobsPaginated(@Parameter(hidden = true) Pageable pageable, @RequestParam(required = false) List<JobStatus> statuses) {
        return new PagedModel<>(jobService.findJobsByStatusPaginated(statuses, pageable));
    }
}