package com.interview.resource;

import com.interview.exception.RepairJobNotFoundException;
import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import com.interview.service.RepairJobService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/api/repair-jobs")
public class RepairJobResource {

    private final RepairJobService service;

    public RepairJobResource(RepairJobService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Search repair jobs by userId, status, and license plate")
    public Page<RepairJob> search(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) RepairStatus status,
            @RequestParam(required = false) String licensePlate,
            @ParameterObject Pageable pageable
    ) {
        log.info("Searching repair jobs: userId={}, status={}, plate={}", userId, status, licensePlate);
        return service.search(userId, status, licensePlate, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a repair job by id")
    public ResponseEntity<RepairJob> getJobById(@PathVariable Long id) {
        var job = service.getJobById(id)
                .orElseThrow(() -> new RepairJobNotFoundException(id));

        return ResponseEntity.ok(job);
    }

    @PostMapping
    @Operation(summary = "Create a repair job")
    public ResponseEntity<RepairJob> createJob(@Valid @RequestBody RepairJob repairJob) {
        var job = service.createJob(repairJob);
        log.info("Saved repair job {}", job);
        return new ResponseEntity<>(job, CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a repair job")
    public ResponseEntity<RepairJob> updateJob(@PathVariable Long id, @Valid @RequestBody RepairJob jobDetails
    ) {
        var updatedJob = service.updateJob(id, jobDetails);
        log.info("Updated repair job {} -> {}", id, updatedJob);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a repair job")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {

        // ensures 404 if missing
        service.getJobById(id)
                .orElseThrow(() -> new RepairJobNotFoundException(id));

        service.deleteJob(id);
        log.info("Deleted repair job {}", id);

        return ResponseEntity.noContent().build();
    }
}
