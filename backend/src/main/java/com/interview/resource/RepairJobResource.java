package com.interview.resource;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import com.interview.service.RepairJobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repair-jobs")
public class RepairJobResource {

    private final RepairJobService service;

    public RepairJobResource(RepairJobService service) {
        this.service = service;
    }

    @GetMapping
    public Page<RepairJob> search(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) RepairStatus status,
            @RequestParam(required = false) String licensePlate,
            @PageableDefault(size = 10, sort = "created") Pageable pageable
    ) {
        return service.search(userId, status, licensePlate, pageable);
    }

    @GetMapping("/{id}")
    public RepairJob getJobById(@PathVariable Long id) {
        return service.getJobById(id);
    }

    @PostMapping
    public RepairJob createJob(@RequestBody RepairJob job) {
        return service.createJob(job);
    }

    @PutMapping("/{id}")
    public RepairJob updateJob(@PathVariable Long id, @RequestBody RepairJob job) {
        return service.updateJob(id, job);
    }

    @DeleteMapping("/{id}")
    public void deleteJob(@PathVariable Long id) {
        service.deleteJob(id);
    }
}
