package com.interview.service;

import com.interview.dto.JobPostingFilter;
import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.exception.IllegalStateTransitionException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.JobPostingMapper;
import com.interview.model.JobPosting;
import com.interview.model.enums.JobStatus;
import com.interview.repository.JobPostingRepository;
import com.interview.repository.JobPostingSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; //
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository repository;
    private final JobPostingMapper mapper;

    @Override
    @Transactional
    public JobPostingResponse create(JobPostingRequest request) {
        log.info("Creating new job posting: {} at {}", request.title(), request.company()); //
        JobPosting posting = mapper.toEntity(request);
        JobPosting saved = repository.save(posting);
        log.debug("Successfully created job posting with ID: {}", saved.getId()); //
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse findById(Long id) {
        log.trace("Fetching job posting by ID: {}", id); //
        return mapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> findAll(JobPostingFilter filter, Pageable pageable) {
        log.debug("Searching job postings with filter: {} and pageable: {}", filter, pageable); //
        Specification<JobPosting> spec = JobPostingSpecification.fromFilter(filter);
        Page<JobPostingResponse> result = repository.findAll(spec, pageable).map(mapper::toResponse);
        log.debug("Found {} job postings", result.getTotalElements()); //
        return result;
    }

    @Override
    @Transactional
    public JobPostingResponse update(Long id, JobPostingRequest request) {
        log.info("Updating job posting ID: {} with title: {}", id, request.title()); //
        JobPosting posting = findOrThrow(id);
        guardAgainstEditingClosed(posting);

        mapper.updateEntity(request, posting);
        JobPosting updated = repository.save(posting);
        log.debug("Successfully updated job posting ID: {}", id); //
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Deleting job posting ID: {}", id); //
        JobPosting posting = findOrThrow(id);
        repository.delete(posting);
        log.info("Successfully deleted job posting ID: {}", id); //
    }

    private JobPosting findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to find job posting with ID: {}", id); //
                    return new ResourceNotFoundException("Job posting not found with id: " + id);
                });
    }

    private void guardAgainstEditingClosed(JobPosting posting) {
        if (posting.getStatus() == JobStatus.CLOSED || posting.getStatus() == JobStatus.ARCHIVED) {
            log.error("Rejecting update: Job posting {} is in state {}", posting.getId(), posting.getStatus()); //
            throw new IllegalStateTransitionException(
                    "Cannot edit a " + posting.getStatus() + " job posting. Create a new posting instead.");
        }
    }
}