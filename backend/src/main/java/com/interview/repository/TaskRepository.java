package com.interview.repository;

import com.interview.model.db.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface TaskRepository extends CrudRepository<Task, Integer> {
}
