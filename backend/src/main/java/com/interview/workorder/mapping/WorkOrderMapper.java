package com.interview.workorder.mapping;

import com.interview.workorder.entity.WorkOrder;
import com.interview.workorder.dto.WorkOrderRequest;
import com.interview.workorder.dto.WorkOrderResponse;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WorkOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "customer", ignore = true)
    WorkOrder toEntity(WorkOrderRequest request);

    @Mapping(target = "customerId", source = "customer.id")
    WorkOrderResponse toResponse(WorkOrder workOrder);

    List<WorkOrderResponse> toResponseList(List<WorkOrder> workOrders);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "customer", ignore = true)
    void updateEntityFromRequest(WorkOrderRequest request, @MappingTarget WorkOrder workOrder);
}
