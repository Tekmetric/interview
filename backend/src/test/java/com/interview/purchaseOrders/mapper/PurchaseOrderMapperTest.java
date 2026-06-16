package com.interview.purchaseOrders.mapper;

import com.interview.purchaseOrders.dto.PurchaseOrderDTO;
import com.interview.purchaseOrders.dto.PurchaseOrderLineDTO;
import com.interview.purchaseOrders.model.PurchaseOrder;
import com.interview.purchaseOrders.model.PurchaseOrderLine;
import com.interview.purchaseOrders.model.PurchaseOrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
class PurchaseOrderMapperTest {

    private PurchaseOrderMapper purchaseOrderMapper = Mappers.getMapper(PurchaseOrderMapper.class);

    @Test
    void testDtoToEntity() {
        //This is an example test case to demonstrate setup, in a real project I would test all the
        //methods in the mapper class to ensure proper conversion.

        //given
        PurchaseOrderDTO purchaseOrderDTO = createTestPurchaseOrder();

        //when
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);

        //then
        Assertions.assertEquals(1L, purchaseOrder.getPurchaseOrderId());
        Assertions.assertEquals("TEST", purchaseOrder.getSupplierName());
        Assertions.assertEquals(LocalDate.of(2025, 12, 1), purchaseOrder.getPlacedOn());
        Assertions.assertEquals(LocalDate.of(2025, 12, 5), purchaseOrder.getExpectedDelivery());
        Assertions.assertEquals(LocalDate.of(2025, 12, 5), purchaseOrder.getActualDelivery());
        Assertions.assertEquals(BigDecimal.valueOf(123.00), purchaseOrder.getTotalCost());
        Assertions.assertEquals(BigDecimal.valueOf(100), purchaseOrder.getTotalWeight());
        Assertions.assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getPurchaseOrderStatus());
        Assertions.assertEquals(1, purchaseOrder.getPurchaseOrderLines().size());

        PurchaseOrderLine purchaseOrderLine = purchaseOrder.getPurchaseOrderLines().get(0);

        Assertions.assertEquals(1L, purchaseOrderLine.getPurchaseOrderLineId());
        Assertions.assertEquals("SKU1", purchaseOrderLine.getSku());
        Assertions.assertEquals("Description", purchaseOrderLine.getDescription());
        Assertions.assertEquals("Blue", purchaseOrderLine.getColor());
        Assertions.assertEquals(5, purchaseOrderLine.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(123.00), purchaseOrderLine.getUnitCost());
        Assertions.assertEquals(BigDecimal.valueOf(100), purchaseOrderLine.getUnitWeight());
    }

    private PurchaseOrderDTO createTestPurchaseOrder() {
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
        purchaseOrderDTO.setPurchaseOrderId(1L);
        purchaseOrderDTO.setSupplierName("TEST");
        purchaseOrderDTO.setPlacedOn(LocalDate.of(2025, 12, 1));
        purchaseOrderDTO.setExpectedDelivery(LocalDate.of(2025, 12, 5));
        purchaseOrderDTO.setActualDelivery(LocalDate.of(2025, 12, 5));
        purchaseOrderDTO.setTotalCost(BigDecimal.valueOf(123.00));
        purchaseOrderDTO.setTotalWeight(BigDecimal.valueOf(100));
        purchaseOrderDTO.setPurchaseOrderStatus(PurchaseOrderStatus.CONFIRMED);

        PurchaseOrderLineDTO purchaseOrderLineDTO = new PurchaseOrderLineDTO();
        purchaseOrderLineDTO.setPurchaseOrderLineId(1L);
        purchaseOrderLineDTO.setPurchaseOrderId(1L);
        purchaseOrderLineDTO.setSku("SKU1");
        purchaseOrderLineDTO.setDescription("Description");
        purchaseOrderLineDTO.setColor("Blue");
        purchaseOrderLineDTO.setQuantity(5);
        purchaseOrderLineDTO.setUnitCost(BigDecimal.valueOf(123.00));
        purchaseOrderLineDTO.setUnitWeight(BigDecimal.valueOf(100));

        purchaseOrderDTO.getPurchaseOrderLines().add(purchaseOrderLineDTO);

        return purchaseOrderDTO;
    }
}
