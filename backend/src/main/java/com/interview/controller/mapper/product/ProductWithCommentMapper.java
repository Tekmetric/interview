package com.interview.controller.mapper.product;


import com.interview.controller.dto.ProductWithCommentsDto;
import com.interview.controller.mapper.CommentMapper;
import com.interview.domain.Product;
import org.mapstruct.Mapper;

@Mapper(uses = {ProductMapper.class, CommentMapper.class}, componentModel = "spring")
public interface ProductWithCommentMapper {
   ProductWithCommentsDto modelToDto(Product product);
}
