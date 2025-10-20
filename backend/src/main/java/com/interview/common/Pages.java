package com.interview.common;

import org.springframework.data.domain.PageRequest;

public class Pages {

    public static final int DEFAULT_SIZE = 20;

    public static PageRequest maxPage() {
        return PageRequest.of(0, Integer.MAX_VALUE);
    }

    public static PageRequest defaultPage() {
        return PageRequest.of(0, 20);
    }
}
