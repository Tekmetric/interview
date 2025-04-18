package com.interview.service;


import com.interview.model.DtoMapper;
import com.interview.model.db.Task;
import com.interview.model.dto.TaskRequest;
import com.interview.model.dto.TaskResponse;
import com.interview.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse findById(Integer id) {
        log.debug("Finding task by id={}", id);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return DtoMapper.Instance.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.debug("Creating task: {}", request);
        Task task = DtoMapper.Instance.toTaskEntity(request);
        task = taskRepository.save(task);
        log.debug("Created new task with id={}: {}", task.getId(), request);
        return DtoMapper.Instance.toTaskResponse(task);

    }

    @Transactional
    public void updateTask(Integer id, TaskRequest request) {
        log.debug("Updating task with id={}: {}", id, request);
        Task task = DtoMapper.Instance.toTaskEntity(request);
        task.setId(id);
        taskRepository.save(task);
        log.debug("Successfully updated task with id={}: {}", id, request);
    }

    @Transactional
    public void deleteTask(Integer id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
        log.info("Successfully deleted task with id: {}", id);
    }
}
