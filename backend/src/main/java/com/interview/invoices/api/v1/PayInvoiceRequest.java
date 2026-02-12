package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.util.Assert;

@Value.Immutable
@JsonSerialize(as = ImmutablePayInvoiceRequest.class)
@JsonDeserialize(as = ImmutablePayInvoiceRequest.class)
public abstract class PayInvoiceRequest {

  public abstract String invoiceNumber();

  public abstract int version();

  public abstract String transactionReference();

  @Value.Check
  public void check() {
    Assert.hasText(transactionReference(), "Transaction reference should not be blank.");
  }
}
