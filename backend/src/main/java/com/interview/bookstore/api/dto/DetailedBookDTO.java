package com.interview.bookstore.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DetailedBookDTO extends BookDTO {
    private String isbn;
    private String description;
    private LocalDate publicationDate;
    private Integer pageCount;
    private List<BookReviewDTO> reviews = new ArrayList<>();
}
