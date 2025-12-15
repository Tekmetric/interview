package com.interview.mappers;

import com.interview.TestUtils;
import com.interview.model.Employee;
import com.interview.model.EmployeeRole;
import com.interview.model.Goal;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.EmployeeRoleEntity;
import com.interview.models.db.GoalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private EmployeeMapper employeeMapper;

    @Test
    void toEmployee_shouldMapAllFieldsAndGoals() {
        // given
        EmployeeEntity entity = TestUtils.createEmployeeEntity();

        String id = "emp-123";
        String name = "Alice Smith";
        String department = "Engineering";
        String email = "alice@example.com";

        entity.setId(id);
        entity.setName(name);
        entity.setDepartment(department);
        entity.setEmail(email);
        entity.setRole(EmployeeRoleEntity.ENGINEER);

        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedAt = OffsetDateTime.now();
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        GoalEntity goalEntity1 = TestUtils.createGoalEntity(entity);
        GoalEntity goalEntity2 = TestUtils.createGoalEntity(entity);
        entity.getGoals().clear();
        entity.getGoals().add(goalEntity1);
        entity.getGoals().add(goalEntity2);

        Goal goal1 = new Goal();
        Goal goal2 = new Goal();

        when(goalMapper.toGoal(goalEntity1)).thenReturn(goal1);
        when(goalMapper.toGoal(goalEntity2)).thenReturn(goal2);

        // when
        Employee result = employeeMapper.toEmployee(entity);

        // then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(department, result.getDepartment());
        assertEquals(email, result.getEmail());
        assertEquals(EmployeeRole.ENGINEER, result.getRole());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());

        assertNotNull(result.getGoals());
        assertEquals(2, result.getGoals().size());
        assertSame(goal1, result.getGoals().get(0));
        assertSame(goal2, result.getGoals().get(1));

        // verify interactions with GoalMapper
        verify(goalMapper, times(1)).toGoal(goalEntity1);
        verify(goalMapper, times(1)).toGoal(goalEntity2);
    }

    @Test
    void toEmployee_shouldHandleEmptyGoalsList() {
        // given
        EmployeeEntity entity = TestUtils.createEmployeeEntity();
        entity.setId("emp-456");
        entity.setName("Bob");
        entity.setDepartment("Sales");
        entity.setEmail("bob@example.com");
        entity.setRole(EmployeeRoleEntity.MANAGER);
        entity.setCreatedAt(OffsetDateTime.now().minusDays(2));
        entity.setUpdatedAt(OffsetDateTime.now());
        entity.getGoals().clear();

        // when
        Employee result = employeeMapper.toEmployee(entity);

        // then
        assertNotNull(result);
        assertEquals("emp-456", result.getId());
        assertEquals("Bob", result.getName());
        assertEquals("Sales", result.getDepartment());
        assertEquals("bob@example.com", result.getEmail());
        assertEquals(EmployeeRole.MANAGER, result.getRole());
        assertNotNull(result.getGoals());
        assertTrue(result.getGoals().isEmpty());

        // goalMapper should never be called
        verifyNoInteractions(goalMapper);
    }
}
