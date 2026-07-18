package com.interview.services;

import com.interview.model.Goal;
import com.interview.model.GoalCreateRequest;
import com.interview.model.GoalUpdateRequest;

import java.util.List;

public interface GoalService {

    Goal createGoal(String employeeId, GoalCreateRequest request);

    void deleteGoal(String employeeId, String goalId);

    Goal getGoalById(String employeeId, String goalId);

    Goal updateGoal(String employeeId, String goalId, GoalUpdateRequest request);

    List<Goal> listGoals(String employeeId);
}
