package com.interview.models.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;

@Value
@Builder
public class ValidationError {

    @JsonProperty("field")
    String field;

    @JsonProperty("message")
    String message;

    @Nullable
    @JsonProperty("rejectedValue")
    Object rejectedValue;

    @JsonCreator
    public ValidationError(
            @JsonProperty("field") String field,
            @JsonProperty("message") String message,
            @JsonProperty("rejectedValue") @Nullable Object rejectedValue
    ) {
        this.field = field;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }
}
