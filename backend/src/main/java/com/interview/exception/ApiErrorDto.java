package com.interview.exception;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ApiErrorDto {

    private String code;
    private String message;
    private int status;
    private String path;
    /**
     * (ISO-8601), e.g. "2026-03-20T12:34:56.789Z".
     */
    private String timestamp;
    private Map<String, String> validationErrors;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public ApiErrorDto() {}

    public ApiErrorDto(
            String code,
            String message,
            int status,
            String path,
            Instant timestamp,
            Map<String, String> validationErrors
    ) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp == null ? null : TIMESTAMP_FORMATTER.format(timestamp);
        this.validationErrors = validationErrors;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
