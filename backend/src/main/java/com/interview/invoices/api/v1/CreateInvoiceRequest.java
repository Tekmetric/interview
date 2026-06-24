package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.immutables.value.Value;
import org.springframework.util.Assert;

@Value.Immutable
@JsonSerialize(as = ImmutableCreateInvoiceRequest.class)
@JsonDeserialize(as = ImmutableCreateInvoiceRequest.class)
public abstract class CreateInvoiceRequest {
  @Value.Default
  public String invoiceNumber() {
    return UUID.randomUUID().toString();
  }

  public abstract String customerId();

  public abstract List<InvoiceItem> items();
  public abstract Optional<String> notes();
  public abstract Optional<String> paymentInstructions();

  @Value.Default
  public LocalDate issueDate() {
    return LocalDate.now();
  }

  public abstract Optional<LocalDate> dueDate();
  public abstract String createdBy();

  @Value.Check
  public void check() {
    Assert.hasText(invoiceNumber(), "'invoiceNumber' must not be blank.");
    Assert.isTrue(invoiceNumber().length() <= 50, "'invoiceNumber' length should be up to 50 symbols.");

    Assert.hasText(customerId(), "'customerId' must not be blank.");
    Assert.isTrue(customerId().length() <= 64, "'customerId' length should be up to 64 symbols.");

    Assert.hasText(createdBy(), "'createdBy' must not be blank.");
    Assert.isTrue(createdBy().length() <= 64, "'createdBy' length should be up to 64 symbols.");

    notes().ifPresent(notes ->
        Assert.isTrue(notes.length() < 4000, "'notes' length should be up to 4000 symbols.")
    );

    paymentInstructions().ifPresent(pi ->
        Assert.isTrue(pi.length() < 4000, "'paymentInstructions' length should be up to 4000 symbols.")
    );

    dueDate().ifPresent(due ->
        Assert.isTrue(!due.isBefore(issueDate()), "'dueDate' must not be before 'issueDate'.")
    );
  }
}
