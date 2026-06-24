package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.util.Assert;

@Value.Immutable
@JsonSerialize(as = ImmutableCancelInvoiceRequest.class)
@JsonDeserialize(as = ImmutableCancelInvoiceRequest.class)
public abstract class CancelInvoiceRequest {

  public abstract String invoiceNumber();

  public abstract int version();

  public abstract String cancelledBy();

  public abstract String comment();

  @Value.Check
  public void check() {
    Assert.hasText(comment(), "Comment is required.");
  }
}
