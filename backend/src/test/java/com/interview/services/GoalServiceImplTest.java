package com.interview.services;

import com.interview.TestUtils;
import com.interview.exceptions.EmployeeNotFoundException;
import com.interview.exceptions.GoalForbiddenException;
import com.interview.exceptions.GoalNotFoundException;
import com.interview.mappers.GoalMapper;
import com.interview.model.Goal;
import com.interview.model.GoalCreateRequest;
import com.interview.model.GoalStatus;
import com.interview.model.GoalUpdateRequest;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.GoalEntity;
import com.interview.models.db.GoalStatusEntity;
import com.interview.repositories.EmployeeRepository;
import com.interview.repositories.GoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    void createGoal_shouldPersistAndReturnMappedGoal() {
        // given
        String employeeId = "emp-123";
        EmployeeEntity employee = TestUtils.createEmployeeEntity();
        employee.setId(employeeId);

        GoalCreateRequest request = new GoalCreateRequest();
        request.setName("Increase Sales");
        request.setDescription("Increase sales by 10% this quarter");
        request.setStatus(GoalStatus.IN_PROGRESS);
        request.setDueDate(OffsetDateTime.now().plusDays(30));

        GoalEntity savedEntity = TestUtils.createGoalEntity(employee);
        savedEntity.setName(request.getName());
        savedEntity.setDescription(request.getDescription());
        savedEntity.setStatus(GoalStatusEntity.IN_PROGRESS);
        savedEntity.setDueDate(request.getDueDate());

        Goal mapped = new Goal();
        mapped.setId(savedEntity.getId());
        mapped.setName(savedEntity.getName());
        mapped.setDescription(savedEntity.getDescription());
        mapped.setStatus(GoalStatus.IN_PROGRESS);
        mapped.setDueDate(savedEntity.getDueDate());

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(goalRepository.save(any(GoalEntity.class))).thenReturn(savedEntity);
        when(goalMapper.toGoal(savedEntity)).thenReturn(mapped);

        // when
        Goal result = goalService.createGoal(employeeId, request);

        // then
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals("Increase Sales", result.getName());
        assertEquals("Increase sales by 10% this quarter", result.getDescription());
        assertEquals(GoalStatus.IN_PROGRESS, result.getStatus());
        assertEquals(savedEntity.getDueDate(), result.getDueDate());

        verify(employeeRepository).findById(employeeId);

        ArgumentCaptor<GoalEntity> goalCaptor = ArgumentCaptor.forClass(GoalEntity.class);
        verify(goalRepository).save(goalCaptor.capture());
        GoalEntity toSave = goalCaptor.getValue();
        assertEquals("Increase Sales", toSave.getName());
        assertEquals("Increase sales by 10% this quarter", toSave.getDescription());
        assertEquals(GoalStatusEntity.IN_PROGRESS, toSave.getStatus());
        assertEquals(request.getDueDate(), toSave.getDueDate());
        assertSame(employee, toSave.getEmployee());

        verify(goalMapper).toGoal(savedEntity);
    }

    @Test
    void createGoal_shouldThrowWhenEmployeeNotFound() {
        // given
        String employeeId = "emp-404";
        GoalCreateRequest request = new GoalCreateRequest();
        request.setName("Some Goal");
        request.setStatus(GoalStatus.IN_PROGRESS);
        request.setDueDate(OffsetDateTime.now().plusDays(10));

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(EmployeeNotFoundException.class,
                () -> goalService.createGoal(employeeId, request));

        verify(employeeRepository).findById(employeeId);
        verifyNoInteractions(goalRepository, goalMapper);
    }

    @Test
    void deleteGoal_shouldDeleteWhenGoalBelongsToEmployee() {
        // given
        String employeeId = "emp-123";
        String goalId = "goal-1";

        EmployeeEntity employee = TestUtils.createEmployeeEntity();
        employee.setId(employeeId);

        GoalEntity goal = TestUtils.createGoalEntity(employee);
        goal.setId(goalId);
        goal.setEmployee(employee);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        // when
        goalService.deleteGoal(employeeId, goalId);

        // then
        verify(goalRepository).findById(goalId);
        verify(goalRepository).delete(goal);
    }

    @Test
    void deleteGoal_shouldThrowNotFoundWhenGoalMissing() {
        // given
        String employeeId = "emp-123";
        String goalId = "missing-goal";

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(GoalNotFoundException.class,
                () -> goalService.deleteGoal(employeeId, goalId));

        verify(goalRepository).findById(goalId);
        verify(goalRepository, never()).delete(any(GoalEntity.class));
    }

    @Test
    void deleteGoal_shouldThrowForbiddenWhenGoalDoesNotBelongToEmployee() {
        // given
        String employeeId = "emp-123";
        String otherEmployeeId = "emp-999";
        String goalId = "goal-1";

        EmployeeEntity owner = TestUtils.createEmployeeEntity();
        owner.setId(otherEmployeeId);

        GoalEntity goal = TestUtils.createGoalEntity(owner);
        goal.setId(goalId);
        goal.setEmployee(owner);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        // when / then
        assertThrows(GoalForbiddenException.class,
                () -> goalService.deleteGoal(employeeId, goalId));

        verify(goalRepository).findById(goalId);
        verify(goalRepository, never()).delete(any(GoalEntity.class));
    }

    @Test
    void getGoalById_shouldReturnMappedGoalWhenOwnedByEmployee() {
        // given
        String employeeId = "emp-123";
        String goalId = "goal-1";

        EmployeeEntity employee = TestUtils.createEmployeeEntity();
        employee.setId(employeeId);

        GoalEntity goalEntity = TestUtils.createGoalEntity(employee);
        goalEntity.setId(goalId);
        goalEntity.setEmployee(employee);

        Goal mapped = new Goal();
        mapped.setId(goalId);
        mapped.setName(goalEntity.getName());
        mapped.setStatus(GoalStatus.IN_PROGRESS);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalEntity));
        when(goalMapper.toGoal(goalEntity)).thenReturn(mapped);

        // when
        Goal result = goalService.getGoalById(employeeId, goalId);

        // then
        assertNotNull(result);
        assertEquals(goalId, result.getId());
        verify(goalRepository).findById(goalId);
        verify(goalMapper).toGoal(goalEntity);
    }

    @Test
    void getGoalById_shouldThrowNotFoundWhenGoalMissing() {
        // given
        String employeeId = "emp-123";
        String goalId = "missing-goal";

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(GoalNotFoundException.class,
                () -> goalService.getGoalById(employeeId, goalId));

        verify(goalRepository).findById(goalId);
        verifyNoInteractions(goalMapper);
    }

    @Test
    void getGoalById_shouldThrowForbiddenWhenGoalNotOwnedByEmployee() {
        // given
        String employeeId = "emp-123";
        String otherEmployeeId = "emp-999";
        String goalId = "goal-1";

        EmployeeEntity owner = TestUtils.createEmployeeEntity();
        owner.setId(otherEmployeeId);

        GoalEntity goalEntity = TestUtils.createGoalEntity(owner);
        goalEntity.setId(goalId);
        goalEntity.setEmployee(owner);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalEntity));

        // when / then
        assertThrows(GoalForbiddenException.class,
                () -> goalService.getGoalById(employeeId, goalId));

        verify(goalRepository).findById(goalId);
        verifyNoInteractions(goalMapper);
    }

    @Test
    void updateGoal_shouldUpdateFieldsAndReturnMappedGoal() {
        // given
        String employeeId = "emp-123";
        String goalId = "goal-1";

        EmployeeEntity employee = TestUtils.createEmployeeEntity();
        employee.setId(employeeId);

        GoalEntity existing = TestUtils.createGoalEntity(employee);
        existing.setId(goalId);
        existing.setEmployee(employee);
        existing.setName("Old Name");
        existing.setDescription("Old Description");
        existing.setStatus(GoalStatusEntity.IN_PROGRESS);
        existing.setDueDate(OffsetDateTime.now().plusDays(10));

        GoalUpdateRequest request = new GoalUpdateRequest();
        request.setName("New Name");
        request.setDescription("New Description");
        request.setStatus(GoalStatus.COMPLETED);
        request.setDueDate(OffsetDateTime.now().plusDays(20));

        GoalEntity saved = TestUtils.createGoalEntity(employee);
        saved.setId(goalId);
        saved.setEmployee(employee);
        saved.setName(request.getName());
        saved.setDescription(request.getDescription());
        saved.setStatus(GoalStatusEntity.COMPLETED);
        saved.setDueDate(request.getDueDate());

        Goal mapped = new Goal();
        mapped.setId(goalId);
        mapped.setName("New Name");
        mapped.setDescription("New Description");
        mapped.setStatus(GoalStatus.COMPLETED);
        mapped.setDueDate(request.getDueDate());

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existing));
        when(goalRepository.save(existing)).thenReturn(saved);
        when(goalMapper.toGoal(saved)).thenReturn(mapped);

        // when
        Goal result = goalService.updateGoal(employeeId, goalId, request);

        // then
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(GoalStatus.COMPLETED, result.getStatus());
        assertEquals(request.getDueDate(), result.getDueDate());

        verify(goalRepository).findById(goalId);
        verify(goalRepository).save(existing);
        verify(goalMapper).toGoal(saved);

        // ensure existing entity was mutated before save
        assertEquals("New Name", existing.getName());
        assertEquals("New Description", existing.getDescription());
        assertEquals(GoalStatusEntity.COMPLETED, existing.getStatus());
        assertEquals(request.getDueDate(), existing.getDueDate());
    }

    @Test
    void updateGoal_shouldThrowNotFoundWhenGoalMissing() {
        // given
        String employeeId = "emp-123";
        String goalId = "missing-goal";

        GoalUpdateRequest request = new GoalUpdateRequest();
        request.setName("Doesn't matter");
        request.setStatus(GoalStatus.IN_PROGRESS);
        request.setDueDate(OffsetDateTime.now().plusDays(5));

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(GoalNotFoundException.class,
                () -> goalService.updateGoal(employeeId, goalId, request));

        verify(goalRepository).findById(goalId);
        verify(goalRepository, never()).save(any(GoalEntity.class));
        verifyNoInteractions(goalMapper);
    }

    @Test
    void updateGoal_shouldThrowForbiddenWhenGoalNotOwnedByEmployee() {
        // given
        String employeeId = "emp-123";
        String otherEmployeeId = "emp-999";
        String goalId = "goal-1";

        EmployeeEntity owner = TestUtils.createEmployeeEntity();
        owner.setId(otherEmployeeId);

        GoalEntity existing = TestUtils.createGoalEntity(owner);
        existing.setId(goalId);
        existing.setEmployee(owner);

        GoalUpdateRequest request = new GoalUpdateRequest();
        request.setName("New Name");
        request.setStatus(GoalStatus.COMPLETED);
        request.setDueDate(OffsetDateTime.now().plusDays(10));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // when / then
        assertThrows(GoalForbiddenException.class,
                () -> goalService.updateGoal(employeeId, goalId, request));

        verify(goalRepository).findById(goalId);
        verify(goalRepository, never()).save(any(GoalEntity.class));
        verifyNoInteractions(goalMapper);
    }

    @Test
    void listGoals_shouldReturnMappedGoalsForEmployee() {
        // given
        String employeeId = "emp-123";

        EmployeeEntity employee = TestUtils.createEmployeeEntity();
        employee.setId(employeeId);

        GoalEntity g1 = TestUtils.createGoalEntity(employee);
        g1.setId("goal-1");
        GoalEntity g2 = TestUtils.createGoalEntity(employee);
        g2.setId("goal-2");

        employee.getGoals().clear();
        employee.getGoals().addAll(Arrays.asList(g1, g2));

        Goal m1 = new Goal();
        m1.setId("goal-1");
        Goal m2 = new Goal();
        m2.setId("goal-2");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(goalMapper.toGoal(g1)).thenReturn(m1);
        when(goalMapper.toGoal(g2)).thenReturn(m2);

        // when
        List<Goal> result = goalService.listGoals(employeeId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("goal-1", result.get(0).getId());
        assertEquals("goal-2", result.get(1).getId());

        verify(employeeRepository).findById(employeeId);
        verify(goalMapper).toGoal(g1);
        verify(goalMapper).toGoal(g2);
    }

    @Test
    void listGoals_shouldThrowWhenEmployeeNotFound() {
        // given
        String employeeId = "emp-404";
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(EmployeeNotFoundException.class,
                () -> goalService.listGoals(employeeId));

        verify(employeeRepository).findById(employeeId);
        verifyNoInteractions(goalMapper);
    }
}
