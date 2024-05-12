package com.interview.bookstore.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookReviewDTO {
    private Long id;
    private Integer score;
    private String text;
}
