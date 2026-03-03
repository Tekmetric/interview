package com.interview.workorder.mapping;

import com.interview.workorder.entity.WorkOrder;
import com.interview.workorder.request.WorkOrderRequest;
import com.interview.workorder.response.WorkOrderResponse;

public class WorkOrderMapping {
    public static void mapRequest(WorkOrder workOrder, WorkOrderRequest request) {
        workOrder.setCustomerName(request.customerName());
        workOrder.setVin(request.vin());
        workOrder.setIssueDescription(request.issueDescription());
        workOrder.setStatus(request.status());
    }

    public static WorkOrderResponse mapResponse(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getCustomerName(),
                workOrder.getVin(),
                workOrder.getIssueDescription(),
                workOrder.getStatus(),
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt()
        );
    }
}
