package com.interview.controller.mapper.product;

import com.interview.controller.dto.ProductDto;
import com.interview.controller.mapper.CommentMapper;
import com.interview.domain.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {CommentMapper.class}, componentModel = "spring")
public interface ProductMapper {

    ProductDto modelToDto(Product product);

    Product dtoToModel(ProductDto productDto);

    List<ProductDto> modelToDtos(List<Product> products);

}
