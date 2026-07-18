package com.interview.mappers;

import com.interview.model.Goal;
import com.interview.model.GoalStatus;
import com.interview.models.db.GoalEntity;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper {
    public Goal toGoal(final GoalEntity entity) {
        final Goal goal = new Goal();
        goal.id(entity.getId());
        goal.name(entity.getName());
        goal.description(entity.getDescription());
        goal.status(GoalStatus.valueOf(entity.getStatus().getValue()));
        goal.dueDate(entity.getDueDate());
        goal.createdAt(entity.getCreatedAt());
        goal.updatedAt(entity.getUpdatedAt());

        return goal;
    }
}
