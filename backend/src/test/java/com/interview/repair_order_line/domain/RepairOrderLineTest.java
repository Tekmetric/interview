package com.interview.repair_order_line.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderLineTest {

    @Test
    void getAmount_null() {
        RepairOrderLine repairOrderLine = new RepairOrderLine();

        BigDecimal result = repairOrderLine.getAmount();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getAmount() {
        RepairOrderLine repairOrderLine = new RepairOrderLine();
        repairOrderLine.setQuantity(BigDecimal.ONE);
        repairOrderLine.setUnitPrice(BigDecimal.valueOf(3.55));

        BigDecimal result = repairOrderLine.getAmount();

        assertEquals(BigDecimal.valueOf(3.55), result);
    }

    @Test
    void getAmount_rounded() {
        RepairOrderLine repairOrderLine = new RepairOrderLine();
        repairOrderLine.setQuantity(BigDecimal.valueOf(2.33));
        repairOrderLine.setUnitPrice(BigDecimal.valueOf(3.55));

        BigDecimal result = repairOrderLine.getAmount(); //8.2715 is rounded to 8.27

        assertEquals(BigDecimal.valueOf(8.27), result);
    }
}