package com.interview.service;

import com.interview.dto.JobPostingFilter;
import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingService {

    JobPostingResponse create(JobPostingRequest request);

    JobPostingResponse findById(Long id);

    Page<JobPostingResponse> findAll(JobPostingFilter filter, Pageable pageable);

    JobPostingResponse update(Long id, JobPostingRequest request);

    void delete(Long id);
}
