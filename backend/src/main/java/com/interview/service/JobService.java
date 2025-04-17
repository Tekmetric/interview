package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.DtoMapper;
import com.interview.model.JobStatus;
import com.interview.model.db.Car;
import com.interview.model.db.Job;
import com.interview.model.dto.JobCreateRequest;
import com.interview.model.dto.JobResponse;
import com.interview.model.dto.JobUpdateRequest;
import com.interview.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;

    private final CarService carService;

    @Transactional
    public Page<JobResponse> findJobsByStatusPaginated(List<JobStatus> statuses, Pageable pageable) {
        if (statuses == null || statuses.isEmpty()) {
            statuses = JobStatus.ALL;
        }
        Page<Integer> idsPage = jobRepository.findAllByStatuses(statuses, pageable);
        Map<Integer, Job> jobsById = jobRepository.findAllByIdWithTasks(idsPage.getContent())
                .stream()
                .collect(Collectors.toMap(
                        Job::getId,
                        Function.identity(),
                        (existingValue, newValue) -> existingValue));
        return idsPage.map(id -> DtoMapper.Instance.toJobResponse(jobsById.get(id)));
    }

    @Transactional
    public List<JobResponse> findAllJobsByCar(String vin) {
        var jobs = jobRepository.findAllByCar_VinOrderByScheduledAtDesc(vin);
        return DtoMapper.Instance.toJobResponses(jobs);
    }

    @Transactional
    public JobResponse findById(Integer id) {
        log.debug("Finding job by id={}", id);
        Job job = jobRepository.findById(id).orElseThrow();
        return DtoMapper.Instance.toJobResponse(job);
    }

    @Transactional
    public JobResponse createJob(JobCreateRequest request) {
        log.debug("Creating new job: {}", request);
        Car car = carService.getOrCreateCar(request);

        Job job = new Job(car, JobStatus.SCHEDULED, request.scheduledAt());
        job = jobRepository.save(job);

        log.debug("Created new job with id={}: {}", job.getId(), request);
        return DtoMapper.Instance.toJobResponse(job);
    }

    @Transactional
    public void updateJob(Integer id, JobUpdateRequest request) {
        log.debug("Updating job with id={}: {}", id, request);
        int updateCount = jobRepository.updateJob(id, request.jobStatus(), request.scheduledAt());

        if (updateCount == 0) {
            log.warn("Could not find job to update with id={}", id);
            throw new ResourceNotFoundException();
        }
        log.debug("Successfully updated job with id={}: {}", id, request);
    }

    @Transactional
    public void deleteJob(Integer id) {
        log.info("Deleting job with id: {}", id);
        var deleteCount = jobRepository.deleteJobById(id);

        if (deleteCount == 0) {
            log.warn("Could not find job to delete with id={}", id);
            throw new ResourceNotFoundException();
        }
        log.info("Successfully deleted job with id: {}", id);
    }

}
