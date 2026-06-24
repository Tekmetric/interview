package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableFindInvoicesResponse.class)
@JsonDeserialize(as = ImmutableFindInvoicesResponse.class)
public abstract class FindInvoicesResponse {

  public abstract List<Invoice> invoices();

  public abstract int pageNumber();

  public abstract int pageSize();

  public abstract int totalPages();

  public abstract InvoiceSortField sortBy();

  public abstract String order();
}
