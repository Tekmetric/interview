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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository repository;
    private final JobPostingMapper mapper;
//todo add readble logs
    @Override
    @Transactional
    public JobPostingResponse create(JobPostingRequest request) {
        JobPosting posting = mapper.toEntity(request);
        return mapper.toResponse(repository.save(posting));
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse findById(Long id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> findAll(JobPostingFilter filter, Pageable pageable) {
        Specification<JobPosting> spec = JobPostingSpecification.fromFilter(filter);
        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public JobPostingResponse update(Long id, JobPostingRequest request) {
        JobPosting posting = findOrThrow(id);
        guardAgainstEditingClosed(posting);
        mapper.updateEntity(request, posting);
        return mapper.toResponse(repository.save(posting));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private JobPosting findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job posting not found with id: " + id));
    }

    private void guardAgainstEditingClosed(JobPosting posting) {
        if (posting.getStatus() == JobStatus.CLOSED
                || posting.getStatus() == JobStatus.ARCHIVED) {
            throw new IllegalStateTransitionException(
                    "Cannot edit a " + posting.getStatus() + " job posting. " +
                    "Create a new posting instead.");
        }
    }
}
