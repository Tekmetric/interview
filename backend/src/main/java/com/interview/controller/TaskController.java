package com.interview.controller;

import com.interview.model.dto.TaskRequest;
import com.interview.model.dto.TaskResponse;
import com.interview.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Operations related to tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get a task by ID",
            description = "Retrieves a specific task based on its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the task",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            })
    public TaskResponse getTask(@Parameter(description = "ID of the task to retrieve", required = true) @PathVariable Integer id) {
        return taskService.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new task",
            description = "Creates a new task.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Task object to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the task",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
            })
    public TaskResponse createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing task",
            description = "Updates an existing task based on its ID.",
            parameters = @Parameter(description = "ID of the task to update", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Task object to be updated",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the task"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            })
    public void updateTask(@PathVariable Integer id, @RequestBody TaskRequest request) {
        taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task by ID",
            description = "Deletes a specific task based on its ID.",
            parameters = @Parameter(description = "ID of the task to delete", required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the task"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            })
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}