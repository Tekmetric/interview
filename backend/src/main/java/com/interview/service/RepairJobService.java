package com.interview.service;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import com.interview.repository.RepairJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.interview.specification.RepairJobSpecifications.userIdEquals;
import static com.interview.specification.RepairJobSpecifications.statusEquals;
import static com.interview.specification.RepairJobSpecifications.licensePlate;
import static org.springframework.data.jpa.domain.Specification.allOf;

@Service
public class RepairJobService {

    private final RepairJobRepository repository;

    public RepairJobService(RepairJobRepository repository) {
        this.repository = repository;
    }

    public List<RepairJob> getAllJobs() {
        return repository.findAll();
    }

    public RepairJob getJobById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair job not found with id " + id));
    }

    public RepairJob createJob(RepairJob job) {
        return repository.save(job);
    }

    public RepairJob updateJob(Long id, RepairJob jobDetails) {
        var job = getJobById(id);
        job.setUserId(jobDetails.getUserId());
        job.setCreated(jobDetails.getCreated());
        job.setRepairDescription(jobDetails.getRepairDescription());
        job.setLicensePlate(jobDetails.getLicensePlate());
        job.setMake(jobDetails.getMake());
        job.setModel(jobDetails.getModel());
        job.setStatus(jobDetails.getStatus());
        return repository.save(job);
    }


    public Page<RepairJob> search(String userId,
                                  RepairStatus status,
                                  String licencePlate,
                                  Pageable pageable) {

        Specification<RepairJob> repairJobSpecification = allOf();

        if (userId != null) {
            repairJobSpecification = repairJobSpecification.and(userIdEquals(userId));
        }

        if (status != null) {
            repairJobSpecification = repairJobSpecification.and(statusEquals(status));
        }

        if (licencePlate != null) {
            repairJobSpecification = repairJobSpecification.and(licensePlate(licencePlate));
        }

        return repository.findAll(repairJobSpecification, pageable);
    }


    public void deleteJob(Long id) {
        repository.deleteById(id);
    }
}

