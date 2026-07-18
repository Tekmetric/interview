package com.interview.services;

import com.interview.exceptions.EmployeeNotFoundException;
import com.interview.exceptions.GoalForbiddenException;
import com.interview.exceptions.GoalNotFoundException;
import com.interview.mappers.GoalMapper;
import com.interview.model.Goal;
import com.interview.model.GoalCreateRequest;
import com.interview.model.GoalUpdateRequest;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.GoalEntity;
import com.interview.models.db.GoalStatusEntity;
import com.interview.repositories.EmployeeRepository;
import com.interview.repositories.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private static final Logger log = LoggerFactory.getLogger(GoalServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final GoalRepository goalRepository;

    private final GoalMapper goalMapper;

    /** EmployeeId could be derived from auth header or similar mechanism, but this service could also be used for
     * internal use such as an Admin or Manager updating a goal on behalf of an employee.
     */
    @Override
    public Goal createGoal(String employeeId, GoalCreateRequest request) {
        try {
            final EmployeeEntity employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

            final GoalEntity entity = GoalEntity.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .name(request.getName())
                    .description(request.getDescription())
                    .status(GoalStatusEntity.fromValue(request.getStatus().name()))
                    .dueDate(request.getDueDate())
                    .employee(employee)
                    .build();

            final GoalEntity saved = goalRepository.save(entity);

            return goalMapper.toGoal(saved);

        } catch (final Exception e) {
            //Add additional context around failures, if necessary, can ignore expected exceptions like EmployeeNotFound
            //to avoid polluting logs (or set lower logging level)
            log.error("Error creating goal for employee {}", employeeId, e);
            throw e;
        }
    }

    @Override
    public void deleteGoal(String employeeId, String goalId) {
        try {
            final GoalEntity goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException("Goal not found: " + goalId));

            if (!goal.getEmployee().getId().equals(employeeId)) {
                throw new GoalForbiddenException("Goal does not belong to employee");
            }

            goalRepository.delete(goal);
        } catch (final Exception e) {
            log.error("Error deleting goal {} for employee {}", goalId, employeeId, e);
            throw e;
        }
    }

    @Override
    public Goal getGoalById(String employeeId, String goalId) {
        try {
            final GoalEntity goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException("Goal not found"));

            if (!goal.getEmployee().getId().equals(employeeId)) {
                throw new GoalForbiddenException("Goal does not belong to this employee");
            }

            return goalMapper.toGoal(goal);
        } catch (final Exception e) {
            log.error("Error fetching goal {} for employee {}", goalId, employeeId, e);
            throw e;
        }
    }

    @Override
    public Goal updateGoal(String employeeId, String goalId, GoalUpdateRequest request) {
        try {
            final GoalEntity entity = goalRepository.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException("Goal not found: " + goalId));

            if (!entity.getEmployee().getId().equals(employeeId)) {
                throw new GoalForbiddenException("Goal does not belong to this employee");
            }

            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setStatus(GoalStatusEntity.fromValue(request.getStatus().name()));
            entity.setDueDate(request.getDueDate());

            final GoalEntity saved = goalRepository.save(entity);

            return goalMapper.toGoal(saved);
        } catch (final Exception e) {
            log.error("Error updating goal {} for employee {}", goalId, employeeId, e);
            throw e;
        }
    }

    @Override
    public List<Goal> listGoals(String employeeId) {
        try {
            final EmployeeEntity employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

            return employee.getGoals().stream()
                    .map(goalMapper::toGoal)
                    .collect(Collectors.toList());
        }  catch (final Exception e) {
            log.error("Error fetching goals for employee {}", employeeId, e);
            throw e;
        }
    }
}
