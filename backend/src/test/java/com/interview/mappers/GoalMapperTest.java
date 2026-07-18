package com.interview.mappers;

import com.interview.TestUtils;
import com.interview.model.Goal;
import com.interview.model.GoalStatus;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.GoalEntity;
import com.interview.models.db.GoalStatusEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class GoalMapperTest {

    private final GoalMapper goalMapper = new GoalMapper();

    @Test
    void toGoal_shouldMapAllFields() {
        // given
        EmployeeEntity employee = TestUtils.createEmployeeEntity();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime dueDate = now.plusDays(10);

        GoalEntity entity = TestUtils.createGoalEntity(employee);
        entity.setId("goal-123");
        entity.setName("Increase Sales");
        entity.setDescription("Increase sales by 10% this quarter");
        entity.setStatus(GoalStatusEntity.IN_PROGRESS); // choose an enum that maps to GoalStatus
        entity.setDueDate(dueDate);
        entity.setCreatedAt(now.minusDays(1));
        entity.setUpdatedAt(now);

        // when
        Goal result = goalMapper.toGoal(entity);

        // then
        assertNotNull(result);
        assertEquals("goal-123", result.getId());
        assertEquals("Increase Sales", result.getName());
        assertEquals("Increase sales by 10% this quarter", result.getDescription());
        assertEquals(GoalStatus.IN_PROGRESS, result.getStatus()); // assuming names line up
        assertEquals(dueDate, result.getDueDate());
        assertEquals(now.minusDays(1), result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    void toGoal_shouldHandleNullDescription() {
        // given
        EmployeeEntity employee = TestUtils.createEmployeeEntity();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime dueDate = now.plusDays(5);

        GoalEntity entity = TestUtils.createGoalEntity(employee);
        entity.setId("goal-456");
        entity.setName("Hire Engineers");
        entity.setDescription(null); // explicitly null
        entity.setStatus(GoalStatusEntity.COMPLETED);
        entity.setDueDate(dueDate);
        entity.setCreatedAt(now.minusDays(2));
        entity.setUpdatedAt(now);

        // when
        Goal result = goalMapper.toGoal(entity);

        // then
        assertNotNull(result);
        assertEquals("goal-456", result.getId());
        assertEquals("Hire Engineers", result.getName());
        assertNull(result.getDescription());
        assertEquals(GoalStatus.COMPLETED, result.getStatus());
        assertEquals(dueDate, result.getDueDate());
        assertEquals(now.minusDays(2), result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }
}
