package com.interview.service;

import com.interview.dto.CreateEmployeeRequest;
import com.interview.dto.EmployeeResponse;
import com.interview.dto.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);

    Page<EmployeeResponse> findAll(Pageable pageable);

    EmployeeResponse findById(Long id);

    EmployeeResponse update(Long id, UpdateEmployeeRequest request);

    void deleteById(Long id);
}
