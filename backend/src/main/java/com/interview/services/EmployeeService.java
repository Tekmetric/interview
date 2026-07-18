package com.interview.services;

import com.interview.model.Employee;
import com.interview.model.EmployeeCreateRequest;
import com.interview.model.EmployeePage;
import com.interview.model.EmployeeUpdateRequest;

public interface EmployeeService {

    Employee createEmployee(EmployeeCreateRequest request);

    void deleteEmployee(String employeeId);

    Employee getEmployeeById(String employeeId);

    Employee updateEmployee(String employeeId, EmployeeUpdateRequest request);

    EmployeePage listEmployees(int page, int size);
}
