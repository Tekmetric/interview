package com.interview.domain;

import java.util.List;

public record WorkOrderDetail(
        WorkOrder workOrder, List<PartLineItem> partLineItems, List<LaborLineItem> laborLineItems) {}
