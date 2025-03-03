package com.interview.repository;

import com.interview.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(Long id);
    Page<Employee> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}