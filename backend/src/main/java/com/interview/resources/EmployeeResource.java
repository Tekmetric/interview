package com.interview.resources;

import com.interview.api.EmployeesApi;
import com.interview.model.Employee;
import com.interview.model.EmployeeCreateRequest;
import com.interview.model.EmployeePage;
import com.interview.model.EmployeeUpdateRequest;
import com.interview.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Validated
public class EmployeeResource implements EmployeesApi {

    private final EmployeeService employeeService;

    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeCreateRequest employeeCreateRequest) {
        final Employee created = employeeService.createEmployee(employeeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> deleteEmployee(String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String employeeId) {
        final Employee employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Employee> updateEmployee(String employeeId,
                                                   @Valid EmployeeUpdateRequest employeeUpdateRequest) {
        final Employee updated = employeeService.updateEmployee(employeeId, employeeUpdateRequest);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<EmployeePage> listEmployees(Integer page,
                                                      Integer size) {
        // Apply simple defaults if not provided
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 20;

        final EmployeePage employeePage = employeeService.listEmployees(pageNumber, pageSize);
        return ResponseEntity.ok(employeePage);
    }
}
