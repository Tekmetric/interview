package com.interview.mappers;

import com.interview.model.Employee;
import com.interview.model.EmployeeRole;
import com.interview.models.db.EmployeeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final GoalMapper goalMapper;

    public Employee toEmployee(final EmployeeEntity entity) {

        final Employee employee = new Employee();
        employee.id(entity.getId());
        employee.name(entity.getName());
        employee.role(EmployeeRole.valueOf(entity.getRole().getValue()));
        employee.department(entity.getDepartment());
        employee.email(entity.getEmail());
        employee.createdAt(entity.getCreatedAt());
        employee.updatedAt(entity.getUpdatedAt());
        employee.goals(entity.getGoals().stream()
                .map(goalMapper::toGoal)
                .collect(Collectors.toList()));

        return employee;
    }
}
