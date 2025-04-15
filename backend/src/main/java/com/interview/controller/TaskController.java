package com.interview.controller;

import com.interview.model.dto.TaskRequest;
import com.interview.model.dto.TaskResponse;
import com.interview.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Integer id) {
        return taskService.getTask(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    public void updateTask(@PathVariable Integer id, @RequestBody TaskRequest request) {
        taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
