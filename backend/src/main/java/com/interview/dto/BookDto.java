package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    private String title;

    private String author;

    private List<ReviewDto> reviews;
}
