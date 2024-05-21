package com.interview.bookstore.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ApiErrorResponse {

    private final ErrorReasonCode reasonCode;
    private final String reasonMessage;
    private final Map<String, Object> properties = new HashMap<>();

    public ApiErrorResponse(ErrorReasonCode reasonCode, String reasonMessage) {
        this.reasonCode = reasonCode;
        this.reasonMessage = reasonMessage;
    }

    @JsonAnySetter
    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }
}
