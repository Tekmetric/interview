package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerPageDto {
    private List<CustomerResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}