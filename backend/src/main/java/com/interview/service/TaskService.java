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
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse getTask(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return DtoMapper.Instance.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        Task task = DtoMapper.Instance.toTaskEntity(request);
        task = taskRepository.save(task);
        return DtoMapper.Instance.toTaskResponse(task);
    }

    @Transactional
    public void updateTask(Integer id, TaskRequest request) {
        Task task = DtoMapper.Instance.toTaskEntity(request);
        task.setId(id);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }

}
