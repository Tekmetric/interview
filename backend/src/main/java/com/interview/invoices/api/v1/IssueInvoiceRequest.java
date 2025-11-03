package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableIssueInvoiceRequest.class)
@JsonDeserialize(as = ImmutableIssueInvoiceRequest.class)
public abstract class IssueInvoiceRequest {
  public abstract String invoiceNumber();
  public abstract int version();
  public abstract String issuedBy();
}
