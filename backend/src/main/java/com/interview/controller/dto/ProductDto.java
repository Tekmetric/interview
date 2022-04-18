package com.interview.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ProductDto {
    private Long id;

    @Size(max = 1000, message = "Description should have max 1000 chars")
    private String description;

    @Size(max = 25, message = "Name max size is 25 characters")
    private String name;

    @PositiveOrZero
    private Double price;

    private String imageUrl;

}
