package com.interview.invoices.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CustomerInvoiceItem {
  @Column(name = "item_code")
  private String itemCode;

  @Column(name = "item_name", nullable = false)
  private String itemName;

  @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
  private BigDecimal quantity;

  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "tax_rate", nullable = false, precision = 5, scale = 4)
  private BigDecimal taxRate;

  public CustomerInvoiceItem() {
  }

  public String getItemCode() {
    return itemCode;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(BigDecimal taxRate) {
    this.taxRate = taxRate;
  }

  @Override
  public String toString() {
    return "CustomerInvoiceItem{" +
           "itemCode='" + itemCode + '\'' +
           ", itemName='" + itemName + '\'' +
           ", quantity=" + quantity +
           ", unitPrice=" + unitPrice +
           ", taxRate=" + taxRate +
           '}';
  }
}
