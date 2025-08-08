package com.interview.mapper;

import com.interview.dto.InventoryItemResponse;
import com.interview.entity.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.category", target = "productCategory")
    @Mapping(source = "product.unit", target = "productUnit")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "warehouse.location", target = "warehouseLocation")
    @Mapping(target = "totalQuantity", ignore = true)
    @Mapping(target = "belowReorderPoint", ignore = true)
    InventoryItemResponse toResponse(InventoryItem inventoryItem);
    
    List<InventoryItemResponse> toResponseList(List<InventoryItem> inventoryItems);
}