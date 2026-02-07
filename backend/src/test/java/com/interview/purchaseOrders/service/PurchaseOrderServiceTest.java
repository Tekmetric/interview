package com.interview.purchaseOrders.service;

import com.interview.purchaseOrders.dto.PurchaseOrderDTO;
import com.interview.purchaseOrders.dto.PurchaseOrderLineDTO;
import com.interview.purchaseOrders.mapper.PurchaseOrderMapper;
import com.interview.purchaseOrders.model.PurchaseOrder;
import com.interview.purchaseOrders.model.PurchaseOrderLine;
import com.interview.purchaseOrders.model.PurchaseOrderStatus;
import com.interview.purchaseOrders.repository.PurchaseOrderRepository;
import com.interview.purchaseOrders.service.impl.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    @Test
    void testFindById() {
        //Note that this test is a bit contrived, but I wanted to demonstrate at least one test case setup. In a real
        //environment, I would test multiple scenarios on all the methods in the service class.

        //given
        PurchaseOrder purchaseOrder = getTestPurchaseOrder();

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));

        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO(1L, "TestSupplier", LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 8), LocalDate.of(2025, 12, 7),
                BigDecimal.valueOf(123.00), BigDecimal.valueOf(10), PurchaseOrderStatus.DELIVERED, new ArrayList<>());

        PurchaseOrderLineDTO purchaseOrderLineDTO = new PurchaseOrderLineDTO(1L, 1L, "124315324323", "Test Item", "blue", 1,
                BigDecimal.valueOf(123), BigDecimal.valueOf(10));

        purchaseOrderDTO.getPurchaseOrderLines().add(purchaseOrderLineDTO);

        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(purchaseOrderDTO);

        //when
        PurchaseOrderDTO returnedDTO = purchaseOrderService.findById(1L);

        //then
        assertEquals(purchaseOrderDTO, returnedDTO);
    }

    private PurchaseOrder getTestPurchaseOrder() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        ReflectionTestUtils.setField(purchaseOrder, "purchaseOrderId", 1L);
        ReflectionTestUtils.setField(purchaseOrder, "supplierName", "Test Supplier");
        ReflectionTestUtils.setField(purchaseOrder, "placedOn", LocalDate.of(2025, 12, 1));
        ReflectionTestUtils.setField(purchaseOrder, "expectedDelivery", LocalDate.of(2025, 12, 8));
        ReflectionTestUtils.setField(purchaseOrder, "actualDelivery", LocalDate.of(2025, 12, 7));
        ReflectionTestUtils.setField(purchaseOrder, "totalCost", BigDecimal.valueOf(123.00));
        ReflectionTestUtils.setField(purchaseOrder, "totalWeight", BigDecimal.valueOf(10));
        ReflectionTestUtils.setField(purchaseOrder, "purchaseOrderStatus", PurchaseOrderStatus.DELIVERED);

        PurchaseOrderLine purchaseOrderLine = new PurchaseOrderLine();
        ReflectionTestUtils.setField(purchaseOrderLine, "purchaseOrderLineId", 1L);
        ReflectionTestUtils.setField(purchaseOrderLine, "purchaseOrder", purchaseOrder);
        ReflectionTestUtils.setField(purchaseOrderLine, "sku", "124315324323");
        ReflectionTestUtils.setField(purchaseOrderLine, "description", "Test Item");
        ReflectionTestUtils.setField(purchaseOrderLine, "color", "blue");
        ReflectionTestUtils.setField(purchaseOrderLine, "quantity", 1);
        ReflectionTestUtils.setField(purchaseOrderLine, "unitCost", BigDecimal.valueOf(123.00));
        ReflectionTestUtils.setField(purchaseOrderLine, "unitWeight", BigDecimal.valueOf(10));

        purchaseOrder.addPurchaseOrderLine(purchaseOrderLine);

        return purchaseOrder;


    }
}
