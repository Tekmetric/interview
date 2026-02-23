package com.interview.service;

import com.interview.dto.CreateEmployeeRequest;
import com.interview.dto.EmployeeResponse;
import com.interview.dto.UpdateEmployeeRequest;
import com.interview.entity.Employee;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        Employee entity = buildEntityFromRequest(request);
        Employee saved = employeeRepository.save(entity);
        return buildResponseFromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::buildResponseFromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return buildResponseFromEntity(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        updateEmployeeEntity(existing, request);
        Employee saved = employeeRepository.save(existing);
        return buildResponseFromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee buildEntityFromRequest(CreateEmployeeRequest request) {
        Employee e = new Employee();
        e.setFirstname(request.getFirstname());
        e.setLastname(request.getLastname());
        e.setHiredDate(request.getHiredDate());
        e.setGender(request.getGender());
        e.setEmploymentStatus(request.getEmploymentStatus());
        e.setTermDate(request.getTermDate());
        e.setYearlySalary(request.getYearlySalary());
        return e;
    }

    private void updateEmployeeEntity(Employee existing, UpdateEmployeeRequest request) {
        if (request.getFirstname() != null) {
            existing.setFirstname(request.getFirstname());
        }
        if (request.getLastname() != null) {
            existing.setLastname(request.getLastname());
        }
        if (request.getHiredDate() != null) {
            existing.setHiredDate(request.getHiredDate());
        }
        if (request.getGender() != null) {
            existing.setGender(request.getGender());
        }
        if (request.getEmploymentStatus() != null) {
            existing.setEmploymentStatus(request.getEmploymentStatus());
        }
        if (request.getTermDate() != null) {
            existing.setTermDate(request.getTermDate());
        }
        if (request.getYearlySalary() != null) {
            existing.setYearlySalary(request.getYearlySalary());
        }
    }

    private EmployeeResponse buildResponseFromEntity(Employee entity) {
        return new EmployeeResponse(
                entity.getId(),
                entity.getVersion(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getHiredDate(),
                entity.getGender(),
                entity.getEmploymentStatus(),
                entity.getTermDate(),
                entity.getYearlySalary()
        );
    }
}
