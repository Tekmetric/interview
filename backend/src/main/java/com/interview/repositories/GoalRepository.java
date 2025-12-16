package com.interview.repositories;

import com.interview.models.db.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, String> {
    //Default repository functions
}
