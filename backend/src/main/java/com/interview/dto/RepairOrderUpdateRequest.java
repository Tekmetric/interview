package com.interview.dto;

import com.interview.entity.RepairOrderStatus;
import java.util.List;

/**
 * We're only allowing some fields of the RepairOrder to be updated for simplicity and to demonstrate that some fields can be immutable.
 */
public record RepairOrderUpdateRequest(
        String customerName,
        String customerPhone,
        RepairOrderStatus status,
        List<RepairLineItemRequest> lineItems
) {}
