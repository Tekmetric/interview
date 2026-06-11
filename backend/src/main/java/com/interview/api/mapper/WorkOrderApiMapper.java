package com.interview.api.mapper;

import com.interview.api.request.WorkOrderRequest;
import com.interview.api.response.LaborLineItemResponse;
import com.interview.api.response.PartLineItemResponse;
import com.interview.api.response.WorkOrderResponse;
import com.interview.api.response.WorkOrderSearchResponse;
import com.interview.domain.WorkOrder;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkOrderApiMapper {

    @Mapping(target = "id", ignore = true)
    WorkOrder toDomain(WorkOrderRequest request);

    WorkOrder toDomain(UUID id, WorkOrderRequest request);

    WorkOrderSearchResponse toSearchResponse(WorkOrder workOrder);

    default WorkOrderResponse toResponse(
            WorkOrder workOrder, List<PartLineItemResponse> partLineItems, List<LaborLineItemResponse> laborLineItems) {
        return new WorkOrderResponse(
                workOrder.id(),
                workOrder.scheduledStartDateTime(),
                workOrder.customerId(),
                workOrder.vehicleId(),
                partLineItems,
                laborLineItems);
    }
}
