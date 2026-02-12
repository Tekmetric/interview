package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableInvoice.class)
@JsonDeserialize(as = ImmutableInvoice.class)
public abstract class Invoice {

  public abstract String invoiceNumber();

  public abstract int version();

  public abstract InvoiceStatus status();

  public abstract String customerId();

  public abstract List<InvoiceItem> items();

  @Value.Derived
  public BigDecimal totalAmount() {
    return items().stream()
        .map(InvoiceItem::total)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public abstract Optional<String> notes();

  public abstract Optional<String> paymentInstructions();

  public abstract LocalDate issueDate();

  public abstract Optional<LocalDate> dueDate();

  public abstract Optional<LocalDate> paidDate();

  @Value.Derived
  public boolean isOverdue() {
    return dueDate()
        .map(due -> status() != InvoiceStatus.PAID
                    && status() != InvoiceStatus.CANCELLED
                    && status() != InvoiceStatus.DRAFT
                    && due.isBefore(LocalDate.now()))
        .orElse(false);
  }

  public abstract Instant createdAt();

  public abstract String createdBy();

  public abstract Instant updatedAt();

  public abstract String updatedBy();
}
