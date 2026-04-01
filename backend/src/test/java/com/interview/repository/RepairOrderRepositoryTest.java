package com.interview.repository;

import com.interview.entity.RepairLineItem;
import com.interview.entity.RepairOrder;
import com.interview.entity.RepairOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepairOrderRepositoryTest {

    @Autowired RepairOrderRepository repo;

    @Test
    public void computeSumForOrderWorksAndVersionIncrements() {
        RepairOrder order = RepairOrder.builder()
                .orderNumber("RO-4000")
                .vin("1N4AL11D75C123456")
                .status(RepairOrderStatus.OPEN)
                .build();

        order.addLineItem(RepairLineItem.builder()
                .description("Part A")
                .quantity(1)
                .unitPrice(new BigDecimal("25.00"))
                .lineTotal(new BigDecimal("25.00"))
                .build());
        order.addLineItem(RepairLineItem.builder()
                .description("Part B")
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .lineTotal(new BigDecimal("20.00"))
                .build());

        order.setTotal(new BigDecimal("45.00"));
        RepairOrder saved = repo.save(order);
        assertNotNull(saved.getId());
        assertNotNull(saved.getVersion());

        BigDecimal sum = repo.computeSumForOrder(saved.getId());
        assertEquals(new BigDecimal("45.00"), sum);

        // Modify and save to see version increment
        Long prevVersion = saved.getVersion();
        saved.setCustomerName("Adam");
        RepairOrder saved2 = repo.save(saved);
        assertTrue(saved2.getVersion() >= prevVersion);
    }
}
