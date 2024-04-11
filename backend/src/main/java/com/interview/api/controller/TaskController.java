package com.interview.controller;

import com.interview.dto.TaskDTO;
import com.interview.service.TaskService;
import com.interview.utils.CreateOperation;
import com.interview.utils.UpdateOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task Controller", description = "Endpoints for managing tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get a task by ID", description = "Retrieves a task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Create a new task", description = "Creates a new task with the provided information")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @PostMapping
    ResponseEntity<TaskDTO> createTask(@Validated(CreateOperation.class) @RequestBody TaskDTO task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
    }

    @Operation(summary = "Override an existing task", description = "Overrides an existing task with the provided information")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @PutMapping
    ResponseEntity<TaskDTO> updateTask(@Validated(UpdateOperation.class) @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskDTO));
    }

    @Operation(summary = "Update a task", description = "Partially updates a task with the provided information")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @PatchMapping
    ResponseEntity<TaskDTO> patchTask(@Validated(UpdateOperation.class) @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok( taskService.patchTask(taskDTO));
    }

    @Operation(summary = "Get a paginated list of tasks", description = "Retrieves a paginated list of tasks")
    @ApiResponse(responseCode = "200", description = "List of tasks retrieved successfully")
    @GetMapping
    Page<TaskDTO> getTasks(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(required = false) String sortDirection) {
        return taskService.getTasks(page, size, sortBy, sortDirection);
    }

    @Operation(summary = "Search tasks by title", description = "Searches for tasks with a specific title")
    @ApiResponse(responseCode = "200", description = "List of tasks matching the title")
    @GetMapping("/search")
    List<TaskDTO> searchTasksByTitle(@RequestParam("title") String title) {
        return taskService.getTasksByTitle(title);
    }

    @Operation(summary = "Delete a task by ID", description = "Deletes a task by its ID")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Task deleted successfully");
    }
}