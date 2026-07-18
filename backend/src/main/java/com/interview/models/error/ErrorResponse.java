package com.interview.models.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class ErrorResponse {

    @JsonProperty("status")
    int status;

    @JsonProperty("error")
    String error;

    @JsonProperty("message")
    String message;

    @JsonProperty("path")
    String path;

    @JsonProperty("errors")
    List<ValidationError> errors;

    @JsonCreator
    public ErrorResponse(
            @JsonProperty("status") int status,
            @JsonProperty("error") String error,
            @JsonProperty("message") String message,
            @JsonProperty("path") String path,
            @JsonProperty("errors") List<ValidationError> errors
    ) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = (errors == null) ? Collections.emptyList() : errors;
    }
}
