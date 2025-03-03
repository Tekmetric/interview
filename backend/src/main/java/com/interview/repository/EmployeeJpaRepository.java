package com.interview.repository;

import com.interview.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class EmployeeJpaRepository implements EmployeeRepository {
    private final EmployeeJpaRepositoryBase jpaRepository;

    public EmployeeJpaRepository(EmployeeJpaRepositoryBase jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Employee save(Employee employee) {
        return jpaRepository.save(employee);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}