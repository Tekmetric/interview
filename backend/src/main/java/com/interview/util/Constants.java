package com.interview.util;

import java.time.format.DateTimeFormatter;

public final class Constants {
    public static final DateTimeFormatter DATE_FORMAT_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String REST_API_PREFIX = "/api";

    private Constants() {

    }
}
