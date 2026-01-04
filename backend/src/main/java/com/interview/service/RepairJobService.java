package com.interview.service;

import com.interview.exception.RepairJobNotFoundException;
import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import com.interview.repository.RepairJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepairJobService {

    private final RepairJobRepository repository;

    public RepairJobService(RepairJobRepository repository) {
        this.repository = repository;
    }

    public List<RepairJob> getAllJobs() {
        return repository.findAll();
    }

    public Optional<RepairJob> getJobById(Long id) {
        return repository.findById(id);
    }

    public RepairJob createJob(RepairJob job) {
        return repository.save(job);
    }

    public RepairJob updateJob(Long id, RepairJob request) {
        var job = getJobById(id)
                .orElseThrow(() -> new RepairJobNotFoundException("Repair job not found with id " + id));

        job.setUserId(request.getUserId());
        job.setJobName(request.getJobName());
        job.setRepairDescription(request.getRepairDescription());
        job.setLicensePlate(request.getLicensePlate());
        job.setMake(request.getMake());
        job.setModel(request.getModel());
        job.setStatus(request.getStatus());
        return repository.save(job);
    }


    public Page<RepairJob> search(String userId,
                                  RepairStatus status,
                                  String licensePlate,
                                  Pageable pageable) {
        return repository.search(userId, status, licensePlate, pageable);
    }


    public void deleteJob(Long id) {
        repository.deleteById(id);
    }
}

