package com.interview.resource;

import com.interview.dto.CreateEmployeeRequest;
import com.interview.dto.EmployeeResponse;
import com.interview.dto.UpdateEmployeeRequest;
import com.interview.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Employees", description = "Employee CRUD operations")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeResource {

    private final EmployeeService employeeService;

    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create employee", description = "Create a new employee. For retry safety, send header Idempotency-Key (e.g. UUID).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "422", description = "Idempotency key reused with different body")
    })
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request
    ) {
        EmployeeResponse created = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "List employees", description = "Returns a paginated list. Query params: page (0-based), size (default 20), sort (e.g. id, lastname,asc).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK (body: content array, totalElements, totalPages, size, number, first, last)")
    })
    @GetMapping
    public Page<EmployeeResponse> list(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return employeeService.findAll(pageable);
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    public EmployeeResponse getById(
            @Parameter(description = "Employee ID") @PathVariable Long id
    ) {
        return employeeService.findById(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        return employeeService.update(id, request);
    }

    @Operation(summary = "Delete employee")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Employee ID") @PathVariable Long id) {
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
