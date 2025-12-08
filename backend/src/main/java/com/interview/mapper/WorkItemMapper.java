package com.interview.mapper;

import com.interview.dto.workitem.CreateWorkItemRequest;
import com.interview.dto.workitem.WorkItemDto;
import com.interview.repository.model.WorkItemEntity;

public class WorkItemMapper {

    private WorkItemMapper() {

    }

    public static WorkItemEntity toEntity(CreateWorkItemRequest request) {
        return WorkItemEntity.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .deleted(false)
                .build();
    }

    public static WorkItemDto toDto(WorkItemEntity entity) {
        return new WorkItemDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice()
        );
    }
}
