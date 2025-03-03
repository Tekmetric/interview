package com.interview.service;

import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.mapper.EntityMapper;
import com.interview.model.Employee;
import com.interview.repository.EmployeeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    @NonNull
    private final EmployeeRepository employeeRepository;
    @NonNull
    private final EntityMapper mapper;

    @Override
    public EmployeeDTO create(CreateEmployeeDTO employeeDTO) {
        log.debug("Creating new employee: {}", employeeDTO);
        Employee employee = mapper.toEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Created new employee with id: {}", savedEmployee.getId());
        return mapper.toDTO(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO findById(Long id) {
        log.debug("Finding employee by id: {}", id);
        return employeeRepository.findById(id)
            .map(mapper::toDTO)
            .orElseThrow(() -> {
                log.error("Employee not found with id: {}", id);
                return new EntityNotFoundException("Employee not found");
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        log.debug("Finding all employees with pagination");
        return employeeRepository.findAll(pageable).map(mapper::toDTO);
    }

    @Override
    public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
        log.debug("Updating employee with id: {}", id);
        Employee existingEmployee = employeeRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Employee not found with id: {}", id);
                return new EntityNotFoundException("Employee not found");
            });

        existingEmployee.setName(employeeDTO.getName());
        existingEmployee.setJobTitle(employeeDTO.getJobTitle());
        existingEmployee.setContactInformation(employeeDTO.getContactInformation());

        Employee savedEmployee = employeeRepository.save(existingEmployee);
        log.info("Updated employee with id: {}", savedEmployee.getId());
        return mapper.toDTO(savedEmployee);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting employee with id: {}", id);
        if (!employeeRepository.existsById(id)) {
            log.error("Employee not found with id: {}", id);
            throw new EntityNotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
        log.info("Deleted employee with id: {}", id);
    }
}