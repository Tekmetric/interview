package com.interview.repository;

import com.interview.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeJpaRepositoryBase extends JpaRepository<Employee, Long> {}