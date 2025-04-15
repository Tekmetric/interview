package com.interview.resource.model;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> data;
    private PaginationMeta meta;

    public PaginatedResponse(List<T> data, PaginationMeta meta) {
        this.data = data;
        this.meta = meta;
    }

    public List<T> getData() {
        return data;
    }

    // Getters and Setters
    public void setData(List<T> data) {
        this.data = data;
    }

    public PaginationMeta getMeta() {
        return meta;
    }

    public void setMeta(PaginationMeta meta) {
        this.meta = meta;
    }
}
