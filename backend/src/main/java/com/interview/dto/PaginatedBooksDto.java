package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedBooksDto implements Serializable {
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private List<BookDto> books;
}
