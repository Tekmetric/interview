package com.interview.api.mapper;

import com.interview.api.dto.TaskDTO;
import com.interview.api.model.Task;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Task model and DTO.
 */
public class TaskMapper {

    static Logger logger = LoggerFactory.getLogger(TaskMapper.class);
    private final TypeMap<TaskDTO, Task> dtoToModelMapper;
    private final TypeMap<Task, TaskDTO> modelToDtoMapper;
    private final UserMapper userMapper;

    public TaskMapper() {
        userMapper = new UserMapper();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        dtoToModelMapper = modelMapper.createTypeMap(TaskDTO.class, Task.class);
        modelToDtoMapper = modelMapper.createTypeMap(Task.class, TaskDTO.class);

        //dtoToModelMapper.addMapping(TaskDTO::getAssignee::getMappings, Task::getAssignee::s));
    }

    public Task toTask(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Task task = dtoToModelMapper.map(taskDTO);

        if (null != taskDTO.getAssignee()) {
            task.setAssignee(userMapper.toUser(taskDTO.getAssignee()));
        }

        if (null != taskDTO.getRequester()) {
            task.setRequester(userMapper.toUser(taskDTO.getRequester()));
        }

        return task;
    }

    public TaskDTO toTaskDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO taskDTO = modelToDtoMapper.map(task);

        taskDTO.setAssignee(userMapper.toUserDTO(task.getAssignee()));

        task.setRequester(userMapper.toUser(taskDTO.getRequester()));


        return taskDTO;
    }

    public List<Task> toTasks(List<TaskDTO> taskDTOs) {
        if (taskDTOs == null) return null;

        return taskDTOs.stream()
                .map(dtoToModelMapper::map)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> toTaskDTOs(List<Task> tasks) {
        if (tasks == null) return null;

        return tasks.stream()
                .map(modelToDtoMapper::map)
                .collect(Collectors.toList());
    }

    public Task copyfrom(TaskDTO from, Task to) {
        dtoToModelMapper.map(from, to);
        return to;
    }
}
