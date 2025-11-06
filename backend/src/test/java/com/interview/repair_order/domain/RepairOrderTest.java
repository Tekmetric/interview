package com.interview.repair_order.domain;

import com.interview.repair_order.api.model.RepairOrderRequest;
import com.interview.repair_order_line.domain.RepairOrderLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairOrderTest {
    private static final String SHOP_ID = "100682ed-abcd-42c4-961d-08d158494d5c";
    private static final String EXTERNAL_RO = "Shop-RO-123";
    private static final Status STATUS = Status.APPROVED;
    private static final Integer ODOMETER_IN = 10;
    private static final Integer ODOMETER_OUT = 20;
    private static final String NOTES = "Notes";

    @Mock
    private RepairOrderLine repairOrderLine1;
    @Mock
    private RepairOrderLine repairOrderLine2;
    @Mock
    private RepairOrderRequest request;

    @Test
    void constructor() {
        when(request.getShopId()).thenReturn(SHOP_ID);
        when(request.getExternalRO()).thenReturn(EXTERNAL_RO);
        when(request.getStatus()).thenReturn(STATUS);
        when(request.getOdometerIn()).thenReturn(ODOMETER_IN);
        when(request.getOdometerOut()).thenReturn(ODOMETER_OUT);
        when(request.getNotes()).thenReturn(NOTES);

        RepairOrder repairOrder = new RepairOrder(request);

        assertNull(repairOrder.getId()); // not set till saved in DB
        assertEquals(SHOP_ID, repairOrder.getShopId());
        assertEquals(EXTERNAL_RO, repairOrder.getExternalRO());
        assertEquals(STATUS, repairOrder.getStatus());
        assertNull(repairOrder.getCreatedAt());
        assertEquals(ODOMETER_IN, repairOrder.getOdometerIn());
        assertEquals(ODOMETER_OUT, repairOrder.getOdometerOut());
        assertEquals(0, repairOrder.getRepairOrderLines().size());
        assertEquals(NOTES, repairOrder.getNotes()); // not set till saved in DB
        assertEquals(BigDecimal.ZERO, repairOrder.getTotalAmount());
    }

    @Test
    void getTotalAmount() {
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setRepairOrderLines(List.of(repairOrderLine1, repairOrderLine2));
        when(repairOrderLine1.getAmount()).thenReturn(BigDecimal.TEN);
        when(repairOrderLine2.getAmount()).thenReturn(BigDecimal.ONE);

        BigDecimal result = repairOrder.getTotalAmount();

        assertEquals(BigDecimal.valueOf(11), result);
    }
}
