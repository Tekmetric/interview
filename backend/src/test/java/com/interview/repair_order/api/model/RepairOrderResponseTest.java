package com.interview.repair_order.api.model;

import com.interview.repair_order.domain.RepairOrder;
import com.interview.repair_order.domain.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairOrderResponseTest {
    private static final String REPAIR_ORDER_ID = "100682ed-01b7-42c4-961d-08d158494d5c";
    private static final String SHOP_ID = "100682ed-abcd-42c4-961d-08d158494d5c";
    private static final String EXTERNAL_RO = "Shop-RO-123";
    private static final Status STATUS = Status.APPROVED;
    private static final Instant CREATED_AT = Instant.ofEpochSecond(1517461200);
    private static final Integer ODOMETER_IN = 10;
    private static final Integer ODOMETER_OUT = 20;
    private static final String NOTES = "Notes";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.40);

    @Mock
    private RepairOrder repairOrder;

    @Test
    void constructor() {
        when(repairOrder.getId()).thenReturn(REPAIR_ORDER_ID);
        when(repairOrder.getShopId()).thenReturn(SHOP_ID);
        when(repairOrder.getExternalRO()).thenReturn(EXTERNAL_RO);
        when(repairOrder.getStatus()).thenReturn(STATUS);
        when(repairOrder.getCreatedDate()).thenReturn(CREATED_AT);
        when(repairOrder.getOdometerIn()).thenReturn(ODOMETER_IN);
        when(repairOrder.getOdometerOut()).thenReturn(ODOMETER_OUT);
        when(repairOrder.getNotes()).thenReturn(NOTES);
        when(repairOrder.getTotalAmount()).thenReturn(AMOUNT);

        RepairOrderResponse response = new RepairOrderResponse(repairOrder);

        assertEquals(REPAIR_ORDER_ID, response.getId());
        assertEquals(SHOP_ID, response.getShopId());
        assertEquals(EXTERNAL_RO, response.getExternalRO());
        assertEquals(STATUS, response.getStatus());
        assertEquals(CREATED_AT, response.getCreatedAt());
        assertEquals(ODOMETER_IN, response.getOdometerIn());
        assertEquals(ODOMETER_OUT, response.getOdometerOut());
        assertEquals(NOTES, response.getNotes());
        assertEquals(AMOUNT, response.getAmount());
    }
}
