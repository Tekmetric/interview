package com.interview;

import com.interview.model.*;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.EmployeeRoleEntity;
import com.interview.models.db.GoalEntity;
import com.interview.models.db.GoalStatusEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class TestUtils {
    public static EmployeeEntity createEmployeeEntity() {
        OffsetDateTime now = OffsetDateTime.now();

        return EmployeeEntity.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Employee")
                .department("Engineering")
                .email("test.employee@example.com")
                .role(EmployeeRoleEntity.MANAGER)  // pick any valid default from your enum
                .createdAt(now)
                .updatedAt(now)
                .goals(new ArrayList<>())
                .build();
    }


    public static GoalEntity createGoalEntity(EmployeeEntity employee) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        return GoalEntity.builder()
                .id(UUID.randomUUID().toString())
                .employee(employee)
                .name("Test Goal")
                .description("Test goal description")
                .status(GoalStatusEntity.IN_PROGRESS)
                .dueDate(now.plusDays(7))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Employee createEmployeeModel() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Employee employee = new Employee();
        employee.setId(UUID.randomUUID().toString());
        employee.setName("Test Employee");
        employee.setDepartment("Engineering");
        employee.setEmail("test.employee@example.com");
        employee.setRole(EmployeeRole.ENGINEER);
        employee.setCreatedAt(now.minusDays(1));
        employee.setUpdatedAt(now);
        employee.setGoals(Collections.emptyList());

        return employee;
    }

    public static EmployeePage createEmployeePage(int page, int size, int totalElements) {
        EmployeePage employeePage = new EmployeePage();
        employeePage.setPage(page);
        employeePage.setSize(size);
        employeePage.setTotalElements(totalElements);

        int totalPages = size > 0
                ? (int) Math.ceil((double) totalElements / size)
                : 0;

        employeePage.setTotalPages(totalPages);
        employeePage.setFirst(page == 0);
        employeePage.setLast(totalPages == 0 || page >= totalPages - 1);
        employeePage.setContent(Collections.singletonList(createEmployeeModel()));

        return employeePage;
    }

    public static Goal createGoalModel() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Goal goal = new Goal();
        goal.setId(UUID.randomUUID().toString());
        goal.setName("Test Goal");
        goal.setDescription("Test goal description");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setDueDate(now.plusDays(7));
        goal.setCreatedAt(now.minusDays(1));
        goal.setUpdatedAt(now);

        return goal;
    }
}
