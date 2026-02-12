package com.interview.services;

import com.interview.exceptions.EmployeeNotFoundException;
import com.interview.mappers.EmployeeMapper;
import com.interview.model.Employee;
import com.interview.model.EmployeeCreateRequest;
import com.interview.model.EmployeePage;
import com.interview.model.EmployeeUpdateRequest;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.EmployeeRoleEntity;
import com.interview.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public Employee createEmployee(EmployeeCreateRequest request) {
        try {
            final EmployeeEntity entity = EmployeeEntity.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .name(request.getName())
                    .role(EmployeeRoleEntity.fromValue(request.getRole().name()))
                    .department(request.getDepartment())
                    .email(request.getEmail())
                    .build();

            final EmployeeEntity saved = employeeRepository.save(entity);

            return employeeMapper.toEmployee(saved);
        } catch (final Exception e) {
            log.error("Error creating employee with name {}", request.getName(), e);
            throw e;
        }
    }

    @Override
    public void deleteEmployee(String employeeId) {
        try {
            if (!employeeRepository.existsById(employeeId)) {
                throw new EmployeeNotFoundException("Employee not found: " + employeeId);
            }

            employeeRepository.deleteById(employeeId);
        } catch (final Exception e) {
            //Add additional context around failures, if necessary, can ignore expected exceptions like EmployeeNotFound
            //to avoid polluting logs (or set lower logging level)
            log.error("Error deleting employee with id {}", employeeId, e);
            throw e;
        }
    }

    @Override
    public Employee getEmployeeById(String employeeId) {
        try {
            final EmployeeEntity entity = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

            return employeeMapper.toEmployee(entity);
        } catch (final Exception e) {
            log.error("Error fetching employee with id {}", employeeId, e);
            throw e;
        }
    }

    @Override
    public Employee updateEmployee(String employeeId, EmployeeUpdateRequest request) {
        try {
            final EmployeeEntity entity = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

            entity.setName(request.getName());
            entity.setRole(EmployeeRoleEntity.fromValue(request.getRole().name()));
            entity.setDepartment(request.getDepartment());
            entity.setEmail(request.getEmail());

            final EmployeeEntity updated = employeeRepository.save(entity);

            return employeeMapper.toEmployee(updated);
        } catch (final Exception e) {
            log.error("Error updating employee with id {}", employeeId, e);
            throw e;
        }
    }

    @Override
    public EmployeePage listEmployees(int page, int size) {
        try {
            final PageRequest pageable = PageRequest.of(page, size);
            final Page<EmployeeEntity> pageResult = employeeRepository.findAll(pageable);

            final EmployeePage employeePage = new EmployeePage();
            employeePage.page(page);
            employeePage.size(size);
            employeePage.totalElements((int) pageResult.getTotalElements());
            employeePage.totalPages(pageResult.getTotalPages());
            employeePage.first(pageResult.isFirst());
            employeePage.last(pageResult.isLast());
            employeePage.content(
                    pageResult.getContent().stream()
                            .map(employeeMapper::toEmployee)
                            .collect(Collectors.toList()));

            return employeePage;
        } catch (final Exception e) {
            log.error("Error fetching employees", e);
            throw e;
        }
    }
}
