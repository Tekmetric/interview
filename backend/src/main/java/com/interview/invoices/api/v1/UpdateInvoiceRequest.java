package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;
import org.springframework.util.Assert;

@Value.Immutable
@JsonSerialize(as = ImmutableUpdateInvoiceRequest.class)
@JsonDeserialize(as = ImmutableUpdateInvoiceRequest.class)
public abstract class UpdateInvoiceRequest {

  public abstract String invoiceNumber();

  public abstract int version();

  public abstract List<InvoiceItem> items();

  public abstract Optional<String> notes();

  public abstract Optional<String> paymentInstructions();

  public abstract LocalDate issueDate();

  public abstract Optional<LocalDate> dueDate();

  public abstract String updatedBy();

  @Value.Check
  public void check() {
    Assert.hasText(invoiceNumber(), "'invoiceNumber' must not be blank.");

    Assert.hasText(updatedBy(), "'updatedBy' must not be blank.");
    Assert.isTrue(updatedBy().length() <= 64, "'updatedBy' length should be up to 64 symbols.");

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
