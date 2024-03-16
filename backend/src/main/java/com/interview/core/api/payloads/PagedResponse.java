package com.interview.core.api.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<Dto> {
    @NotNull
    private List<Dto> data;
    @NotNull
    private Integer page;
    @NotNull
    private Integer limit;
    @NotNull
    private Long totalItems;
    @NotNull
    private Integer totalPages;
}
