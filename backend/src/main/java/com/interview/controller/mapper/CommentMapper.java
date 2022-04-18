package com.interview.controller.mapper;

import com.interview.controller.dto.CommentDto;
import com.interview.domain.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto modelToDto(Comment model);

    Comment dtoToModel(CommentDto dto);

    List<CommentDto> modelToDtos(List<Comment> models);
}
