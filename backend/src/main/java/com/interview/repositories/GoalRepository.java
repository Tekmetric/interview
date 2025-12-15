package com.interview.repositories;

import com.interview.models.db.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, String> {

    // All goals for a given employee
    List<GoalEntity> findByEmployeeId(String employeeId);

    // Check if a goal belongs to an employee
    boolean existsByIdAndEmployeeId(String id, String employeeId);

    // Optionally, a combined lookup helper
    Optional<GoalEntity> findByIdAndEmployeeId(String id, String employeeId);
}
