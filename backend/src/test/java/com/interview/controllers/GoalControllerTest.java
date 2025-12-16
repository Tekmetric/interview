package com.interview.controllers;

import com.interview.TestUtils;
import com.interview.model.Goal;
import com.interview.model.GoalCreateRequest;
import com.interview.model.GoalStatus;
import com.interview.model.GoalUpdateRequest;
import com.interview.services.GoalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    @Test
    void createEmployeeGoal_shouldReturnCreatedGoalWith201Status() {
        // given
        String employeeId = "emp-123";

        GoalCreateRequest request = new GoalCreateRequest();
        request.setName("Increase Sales");
        request.setDescription("Increase sales by 10% this quarter");
        request.setStatus(GoalStatus.IN_PROGRESS);
        request.setDueDate(OffsetDateTime.now().plusDays(30));

        Goal created = TestUtils.createGoalModel();
        created.setName("Increase Sales");
        created.setDescription("Increase sales by 10% this quarter");
        created.setStatus(GoalStatus.IN_PROGRESS);
        created.setDueDate(request.getDueDate());

        when(goalService.createGoal(employeeId, request)).thenReturn(created);

        // when
        ResponseEntity<Goal> response = goalController.createEmployeeGoal(employeeId, request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());

        ArgumentCaptor<GoalCreateRequest> captor = ArgumentCaptor.forClass(GoalCreateRequest.class);
        verify(goalService).createGoal(eq(employeeId), captor.capture());
        GoalCreateRequest passed = captor.getValue();
        assertEquals("Increase Sales", passed.getName());
        assertEquals("Increase sales by 10% this quarter", passed.getDescription());
        assertEquals(GoalStatus.IN_PROGRESS, passed.getStatus());
        assertEquals(request.getDueDate(), passed.getDueDate());
    }

    @Test
    void deleteEmployeeGoal_shouldReturn204AndDelegateToService() {
        // given
        String employeeId = "emp-123";
        String goalId = "goal-1";

        // when
        ResponseEntity<Void> response = goalController.deleteEmployeeGoal(employeeId, goalId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(goalService).deleteGoal(employeeId, goalId);
    }

    @Test
    void getEmployeeGoal_shouldReturnGoalWith200Status() {
        // given
        String employeeId = "emp-123";
        String goalId = "goal-1";

        Goal goal = TestUtils.createGoalModel();
        goal.setId(goalId);

        when(goalService.getGoalById(employeeId, goalId)).thenReturn(goal);

        // when
        ResponseEntity<Goal> response = goalController.getEmployeeGoal(employeeId, goalId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(goal, response.getBody());

        verify(goalService).getGoalById(employeeId, goalId);
    }

    @Test
    void updateEmployeeGoal_shouldReturnUpdatedGoalWith200Status() {
        // given
        String employeeId = "emp-123";
        String goalId = "goal-1";

        GoalUpdateRequest request = new GoalUpdateRequest();
        request.setName("Updated Goal");
        request.setDescription("Updated description");
        request.setStatus(GoalStatus.COMPLETED);
        request.setDueDate(OffsetDateTime.now().plusDays(15));

        Goal updated = TestUtils.createGoalModel();
        updated.setId(goalId);
        updated.setName("Updated Goal");
        updated.setDescription("Updated description");
        updated.setStatus(GoalStatus.COMPLETED);
        updated.setDueDate(request.getDueDate());

        when(goalService.updateGoal(employeeId, goalId, request)).thenReturn(updated);

        // when
        ResponseEntity<Goal> response = goalController.updateEmployeeGoal(employeeId, goalId, request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());

        verify(goalService).updateGoal(employeeId, goalId, request);
    }

    @Test
    void listEmployeeGoals_shouldReturnGoalsWith200Status() {
        // given
        String employeeId = "emp-123";

        Goal g1 = TestUtils.createGoalModel();
        g1.setId("goal-1");
        Goal g2 = TestUtils.createGoalModel();
        g2.setId("goal-2");

        List<Goal> goals = Arrays.asList(g1, g2);

        when(goalService.listGoals(employeeId)).thenReturn(goals);

        // when
        ResponseEntity<List<Goal>> response = goalController.listEmployeeGoals(employeeId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(goals, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(goalService).listGoals(employeeId);
    }
}
