package com.interview.mapper;

import com.interview.domain.RepairOrder;
import com.interview.dto.RepairOrderCreateDto;
import com.interview.dto.RepairOrderDto;

public final class RepairOrderMapper {

    public static RepairOrder toEntity(RepairOrderCreateDto request) {
        return new RepairOrder(request.getCustomerName(), request.getDescription(), request.getStatus());
    }

    public static RepairOrderDto toDto(RepairOrder entity) {
        return new RepairOrderDto(
                entity.getId(),
                entity.getVersion(),
                entity.getCustomerName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
