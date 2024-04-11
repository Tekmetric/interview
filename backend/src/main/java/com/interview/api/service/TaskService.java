package com.interview.service;

import com.interview.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * This interface defines operations for managing tasks.
 */
@Service
public interface TaskService {

    /**
     * Creates a new task.
     *
     * @param taskDTO The TaskDTO object containing task information to be created.
     * @return The TaskDTO object representing the created task.
     */
    TaskDTO createTask(TaskDTO taskDTO);

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id The unique identifier of the task to retrieve.
     * @return The TaskDTO object representing the retrieved task.
     */
    TaskDTO getTaskById(Long id);

    /**
     * Retrieves a paginated list of tasks.
     *
     * @param pageNumber    The page number of the result set to retrieve.
     * @param pageSize      The maximum number of tasks per page.
     * @param sortBy        The field to sort the tasks by.
     * @param sortDirection The direction of sorting (ASC or DESC).
     * @return A Page object containing a subset of TaskDTO objects representing the tasks.
     */
    Page<TaskDTO> getTasks(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

    /**
     * Retrieves a list of tasks by their title.
     *
     * @param title The title of the tasks to retrieve.
     * @return A List of TaskDTO objects representing the tasks with the specified title.
     */
    List<TaskDTO> getTasksByTitle(String title);

    /**
     * Updates an existing task.
     *
     * @param taskDTO The TaskDTO object containing updated task information.
     * @return The TaskDTO object representing the updated task.
     */
    TaskDTO updateTask(TaskDTO taskDTO);

    /**
     * Partially updates an existing task.
     *
     * @param taskDTO The TaskDTO object containing partial task information to be updated.
     * @return The TaskDTO object representing the updated task.
     */
    TaskDTO patchTask(TaskDTO taskDTO);

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id The unique identifier of the task to delete.
     */
    void deleteTask(Long id);
}
