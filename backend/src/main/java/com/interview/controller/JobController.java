package com.interview.controller;


import com.interview.model.JobStatus;
import com.interview.model.dto.JobCreateRequest;
import com.interview.model.dto.JobResponse;
import com.interview.model.dto.JobUpdateRequest;
import com.interview.service.JobService;
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
public class JobController {

    private final JobService jobService;

    @GetMapping("/car/{vin}")
    @PreAuthorize("hasRole('USER')")
    public List<JobResponse> findAllJobsByCar(@PathVariable String vin) {
        return jobService.findAllJobsByCar(vin);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public PagedModel<JobResponse> findAllJobsPaginated(Pageable pageable, @RequestParam(required = false) List<JobStatus> statuses) {
        return new PagedModel<>(jobService.findJobsByStatusPaginated(statuses, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public JobResponse jobs(@PathVariable Integer id) {
        return jobService.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public JobResponse createJob(@RequestBody JobCreateRequest jobCreateRequest) {
        return jobService.createJob(jobCreateRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateJob(@PathVariable Integer id, @RequestBody JobUpdateRequest jobUpdateRequest) {
        jobService.updateJob(id, jobUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteJob(@PathVariable Integer id) {
        jobService.deleteJob(id);
    }
}
