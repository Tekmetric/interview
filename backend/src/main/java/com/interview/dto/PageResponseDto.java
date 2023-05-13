package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponseDto<T> {

    private List<T> content;

    private int page;

    private int pageSize;

    private long totalNumberOfElements;

}

