package com.interview.invoices.api.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.immutables.value.Value;
import org.springframework.util.Assert;

@Value.Immutable
@JsonSerialize(as = ImmutableInvoiceItem.class)
@JsonDeserialize(as = ImmutableInvoiceItem.class)
public abstract class InvoiceItem {
  public abstract String itemCode();
  public abstract String itemName();
  public abstract BigDecimal quantity();
  public abstract BigDecimal unitPrice();
  public abstract BigDecimal taxRate();

  public InvoiceItem() {
  }

  @Value.Derived
  public BigDecimal subTotal() {
    return scaleMonetaryAmount(quantity().multiply(unitPrice()));
  }

  @Value.Derived
  public BigDecimal taxTotal() {
    return scaleMonetaryAmount(subTotal().multiply(taxRate()));
  }

  @Value.Derived
  public BigDecimal total() {
    return scaleMonetaryAmount(subTotal().add(taxTotal()));
  }

  private static BigDecimal scaleMonetaryAmount(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_UP);
  }

  @Value.Check
  public void check() {
    Assert.hasText(itemName(), "'itemName' must not be blank.");
    Assert.isTrue(itemName().length() <= 250, "'itemName' length should be up to 250 symbols.");

    if (itemCode() != null) {
      Assert.isTrue(itemCode().length() <= 30, "'itemCode' length should be up to 30 symbols.");
    }

    Assert.isTrue(quantity().compareTo(BigDecimal.ZERO) > 0, "'quantity' must be greater than 0.");

    Assert.isTrue(unitPrice().compareTo(BigDecimal.ZERO) >= 0, "'unitPrice' must be greater than or equal to 0.");

    Assert.isTrue(
        taxRate().compareTo(BigDecimal.ZERO) >= 0 && taxRate().compareTo(BigDecimal.valueOf(5)) <= 0,
        "'taxRate' must be between 0 and 5."
    );
  }
}
