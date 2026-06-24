package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Check;
import org.springframework.util.Assert;

@Value.Immutable
@JsonSerialize(as = ImmutableDeleteInvoiceRequest.class)
@JsonDeserialize(as = ImmutableDeleteInvoiceRequest.class)
public abstract class DeleteInvoiceRequest {

  public abstract String invoiceNumber();

  public abstract int version();

  @Check
  public void check() {
    Assert.hasText(invoiceNumber(), "'invoiceNumber' must not be blank.");
  }
}
