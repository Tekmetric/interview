package com.interview.purchaseOrders.mapper;

import com.interview.purchaseOrders.dto.PurchaseOrderDTO;
import com.interview.purchaseOrders.dto.PurchaseOrderLineDTO;
import com.interview.purchaseOrders.model.PurchaseOrder;
import com.interview.purchaseOrders.model.PurchaseOrderLine;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    PurchaseOrderDTO toDto(PurchaseOrder purchaseOrder);

    PurchaseOrder toEntity(PurchaseOrderDTO purchaseOrderDTO);

    @Mapping(target = "purchaseOrderLines", ignore = true)
    void updateEntityFromDTO(PurchaseOrderDTO purchaseOrderDTO, @MappingTarget PurchaseOrder purchaseOrder);

    @Mapping(target = "purchaseOrderId", source = "purchaseOrder.purchaseOrderId")
    PurchaseOrderLineDTO toDto(PurchaseOrderLine purchaseOrderLine);

    @Mapping(target = "purchaseOrder", source = "purchaseOrderId")
    PurchaseOrderLine toEntity(PurchaseOrderLineDTO purchaseOrderLineDTO);

    default PurchaseOrder map(Long purchaseOrderId) {
        if (purchaseOrderId == null) {
            return null;
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderId(purchaseOrderId);
        return purchaseOrder;
    }

    @AfterMapping
    default void linkPurchaseOrderLines(@MappingTarget PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderLines() != null) {
            purchaseOrder.getPurchaseOrderLines().forEach(purchaseOrderLine -> purchaseOrderLine.setPurchaseOrder(purchaseOrder));
        }
    }
}
