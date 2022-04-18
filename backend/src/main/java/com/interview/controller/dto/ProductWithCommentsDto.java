package com.interview.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductWithCommentsDto extends ProductDto{
    private List<CommentDto> comments;
}
