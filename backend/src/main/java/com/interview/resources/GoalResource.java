package com.interview.resources;

import com.interview.api.GoalsApi;
import com.interview.model.Goal;
import com.interview.model.GoalCreateRequest;
import com.interview.model.GoalUpdateRequest;
import com.interview.services.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class GoalResource implements GoalsApi {

    private final GoalService goalService;

    public GoalResource(GoalService goalService) {
        this.goalService = goalService;
    }

    @Override
    public ResponseEntity<Goal> createEmployeeGoal(String employeeId,
                                                   @Valid GoalCreateRequest goalCreateRequest) {
        final Goal created = goalService.createGoal(employeeId, goalCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> deleteEmployeeGoal(String employeeId, String goalId) {
        goalService.deleteGoal(employeeId, goalId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Goal> getEmployeeGoal(String employeeId, String goalId) {
        final Goal goal = goalService.getGoalById(employeeId, goalId);
        return ResponseEntity.ok(goal);
    }

    @Override
    public ResponseEntity<Goal> updateEmployeeGoal(String employeeId,
                                                   String goalId,
                                                   @Valid GoalUpdateRequest goalUpdateRequest) {
        final Goal updated = goalService.updateGoal(employeeId, goalId, goalUpdateRequest);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<List<Goal>> listEmployeeGoals(String employeeId) {
        final List<Goal> goals = goalService.listGoals(employeeId);
        return ResponseEntity.ok(goals);
    }
}
