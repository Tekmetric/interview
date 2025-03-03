package com.interview.controller;

import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Operations pertaining to employees in the shelter")
public class EmployeeController {
    @NonNull
    private final EmployeeService employeeService;

    @Operation(summary = "Create a new employee", description = "Creates a new employee in the shelter system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(
            @Parameter(description = "Employee to create", required = true)
            @Valid @RequestBody CreateEmployeeDTO employeeDTO) {
        log.debug("REST request to create Employee: {}", employeeDTO);
        EmployeeDTO result = employeeService.create(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Get an employee by ID", description = "Returns a single employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the employee"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(
            @Parameter(description = "ID of employee to be obtained", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get Employee: {}", id);
        EmployeeDTO employeeDTO = employeeService.findById(id);
        return ResponseEntity.ok(employeeDTO);
    }

    @Operation(
        summary = "Get all employees",
        description = "Returns all employees in the system with pagination. " +
                     "Use page and size parameters for pagination. " +
                     "Use sortBy and sortDirection for sorting (e.g., sortBy=name&sortDirection=ASC)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved employees")
    })
    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getAllEmployees(
            @Parameter(
                description = "Page number (0-based)",
                schema = @Schema(type = "integer", defaultValue = "0")
            )
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                description = "Size of page",
                schema = @Schema(type = "integer", defaultValue = "10")
            )
            @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(
                description = "Property to sort by (e.g., name, jobTitle)",
                schema = @Schema(type = "string")
            )
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @Parameter(
                description = "Sort direction (ASC or DESC)",
                schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
            )
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        log.debug("REST request to get all Employees with pagination: page={}, size={}, sortBy={}, sortDirection={}", 
            page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeDTO> result = employeeService.findAll(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update an existing employee", description = "Updates an employee's information in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @Parameter(description = "ID of employee to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated employee details", required = true)
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.debug("REST request to update Employee: {}", id);
        EmployeeDTO result = employeeService.update(id, employeeDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete an employee", description = "Removes an employee from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID of employee to delete", required = true)
            @PathVariable Long id) {
        log.debug("REST request to delete Employee: {}", id);
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}