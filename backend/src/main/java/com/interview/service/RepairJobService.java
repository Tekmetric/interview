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

    public List<RepairJob> getAllRepairJobs() {
        return repository.findAll();
    }

    public Optional<RepairJob> getRepairJobById(Long id) {
        return repository.findById(id);
    }

    public RepairJob createRepairJob(RepairJob job) {
        return repository.save(job);
    }

    public RepairJob updateRepairJob(Long id, RepairJob request) {
        var job = getRepairJobById(id)
                .orElseThrow(() -> new RepairJobNotFoundException("Repair job not found with id " + id));

        job.setUserId(request.getUserId());
        job.setName(request.getName());
        job.setRepairDescription(request.getRepairDescription());
        job.setLicensePlate(request.getLicensePlate());
        job.setMake(request.getMake());
        job.setModel(request.getModel());
        job.setStatus(request.getStatus());
        return repository.save(job);
    }


    public Page<RepairJob> searchRepairJobs(String userId,
                                            RepairStatus status,
                                            String licensePlate,
                                            Pageable pageable) {
        return repository.search(userId, status, licensePlate, pageable);
    }


    public void deleteRepairJob(Long id) {
        repository.deleteById(id);
    }
}

