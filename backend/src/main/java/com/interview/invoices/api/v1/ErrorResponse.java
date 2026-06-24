package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableErrorResponse.class)
@JsonDeserialize(as = ImmutableErrorResponse.class)
public abstract class ErrorResponse {

  @Value.Parameter
  public abstract String code();

  @Value.Parameter
  public abstract String message();
}
