package com.interview.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerPageDto {
    private List<CustomerDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}