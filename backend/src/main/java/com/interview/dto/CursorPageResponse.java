package com.interview.dto;

import java.util.List;

public class CursorPageResponse<T> {
    private List<T> data;
    private String nextCursor;
    private boolean hasMore;
    private int pageSize;

    public CursorPageResponse() {
    }

    public CursorPageResponse(List<T> data, String nextCursor, boolean hasMore, int pageSize) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
