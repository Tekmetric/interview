package com.interview.repository.mapper;

import com.interview.domain.WorkOrder;
import com.interview.repository.entity.WorkOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface WorkOrderEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer")
    @Mapping(source = "vehicleId", target = "vehicle")
    @Mapping(target = "partLineItems", ignore = true)
    @Mapping(target = "laborLineItems", ignore = true)
    WorkOrderEntity toEntity(WorkOrder workOrder);

    @Mapping(source = "customer", target = "customerId")
    @Mapping(source = "vehicle", target = "vehicleId")
    WorkOrder toDomain(WorkOrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer")
    @Mapping(source = "vehicleId", target = "vehicle")
    @Mapping(target = "partLineItems", ignore = true)
    @Mapping(target = "laborLineItems", ignore = true)
    void updateEntity(WorkOrder workOrder, @MappingTarget WorkOrderEntity entity);
}
