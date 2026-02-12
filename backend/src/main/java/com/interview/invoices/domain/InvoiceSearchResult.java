package com.interview.invoices.domain;

import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public abstract class InvoiceSearchResult {
  public abstract List<CustomerInvoice> invoices();
  public abstract int pageNumber();
  public abstract int pageSize();
  public abstract int totalPages();
}
