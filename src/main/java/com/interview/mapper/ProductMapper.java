package com.interview.mapper;

import com.interview.dto.ProductCreateRequest;
import com.interview.dto.ProductResponse;
import com.interview.dto.ProductUpdateRequest;
import com.interview.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    Product toEntity(ProductCreateRequest request);
    
    Product toEntity(ProductUpdateRequest request);
    
    ProductResponse toResponse(Product product);
    
    List<ProductResponse> toResponseList(List<Product> products);
    
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}