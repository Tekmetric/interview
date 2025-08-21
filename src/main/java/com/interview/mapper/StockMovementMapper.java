package com.interview.mapper;

import com.interview.dto.StockMovementResponse;
import com.interview.entity.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    StockMovementResponse toResponse(StockMovement stockMovement);
    
    List<StockMovementResponse> toResponseList(List<StockMovement> stockMovements);
}