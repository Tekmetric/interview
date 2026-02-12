package com.interview.controllers;

import com.interview.TestUtils;
import com.interview.model.*;
import com.interview.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void createEmployee_shouldReturnCreatedEmployeeWith201Status() {
        // given
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setName("Alice");
        request.setDepartment("Engineering");
        request.setEmail("alice@example.com");
        request.setRole(EmployeeRole.ENGINEER);

        Employee created = TestUtils.createEmployeeModel();
        created.setName("Alice");
        created.setDepartment("Engineering");
        created.setEmail("alice@example.com");
        created.setRole(EmployeeRole.ENGINEER);

        when(employeeService.createEmployee(request)).thenReturn(created);

        // when
        ResponseEntity<Employee> response = employeeController.createEmployee(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());

        ArgumentCaptor<EmployeeCreateRequest> captor = ArgumentCaptor.forClass(EmployeeCreateRequest.class);
        verify(employeeService).createEmployee(captor.capture());
        EmployeeCreateRequest passed = captor.getValue();
        assertEquals("Alice", passed.getName());
        assertEquals("Engineering", passed.getDepartment());
        assertEquals("alice@example.com", passed.getEmail());
        assertEquals(EmployeeRole.ENGINEER, passed.getRole());
    }

    @Test
    void deleteEmployee_shouldReturn204AndDelegateToService() {
        // given
        String employeeId = "emp-123";

        // when
        ResponseEntity<Void> response = employeeController.deleteEmployee(employeeId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(employeeService).deleteEmployee(employeeId);
    }

    @Test
    void getEmployeeById_shouldReturnEmployeeWith200Status() {
        // given
        String employeeId = "emp-123";
        Employee employee = TestUtils.createEmployeeModel();
        employee.setId(employeeId);

        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);

        // when
        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee, response.getBody());

        verify(employeeService).getEmployeeById(employeeId);
    }

    @Test
    void updateEmployee_shouldReturnUpdatedEmployeeWith200Status() {
        // given
        String employeeId = "emp-123";

        EmployeeUpdateRequest request = new EmployeeUpdateRequest();
        request.setName("Updated Name");
        request.setRole(EmployeeRole.MANAGER);
        request.setDepartment("New Dept");
        request.setEmail("new@example.com");

        Employee updated = TestUtils.createEmployeeModel();
        updated.setId(employeeId);
        updated.setName("Updated Name");
        updated.setRole(EmployeeRole.MANAGER);
        updated.setDepartment("New Dept");
        updated.setEmail("new@example.com");

        when(employeeService.updateEmployee(employeeId, request)).thenReturn(updated);

        // when
        ResponseEntity<Employee> response = employeeController.updateEmployee(employeeId, request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());

        verify(employeeService).updateEmployee(employeeId, request);
    }

    @Test
    void listEmployees_shouldUseProvidedPageAndSizeAndReturnPage() {
        // given
        Integer page = 1;
        Integer size = 10;

        EmployeePage employeePage = TestUtils.createEmployeePage(page, size, 25);

        when(employeeService.listEmployees(page, size)).thenReturn(employeePage);

        // when
        ResponseEntity<EmployeePage> response = employeeController.listEmployees(page, size);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeePage, response.getBody());

        verify(employeeService).listEmployees(page, size);
    }

    @Test
    void listEmployees_shouldApplyDefaultsWhenPageAndSizeAreNull() {
        // given
        Integer page = null;
        Integer size = null;

        int expectedPage = 0;
        int expectedSize = 20;

        EmployeePage employeePage = TestUtils.createEmployeePage(expectedPage, expectedSize, 5);

        when(employeeService.listEmployees(expectedPage, expectedSize)).thenReturn(employeePage);

        // when
        ResponseEntity<EmployeePage> response = employeeController.listEmployees(page, size);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeePage, response.getBody());

        verify(employeeService).listEmployees(expectedPage, expectedSize);
    }
}
