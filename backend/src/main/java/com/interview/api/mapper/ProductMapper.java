package com.interview.api.mapper;

import com.interview.api.model.ProductDto;
import com.interview.dao.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	ProductDto productToDto(Product product);

	Product dtoToProduct(ProductDto productDto);
}
