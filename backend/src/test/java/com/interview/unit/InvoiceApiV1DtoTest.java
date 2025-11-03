package com.interview.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.interview.invoices.api.v1.ImmutableInvoice;
import com.interview.invoices.api.v1.ImmutableInvoiceItem;
import com.interview.invoices.api.v1.Invoice;
import com.interview.invoices.api.v1.InvoiceItem;
import com.interview.invoices.api.v1.InvoiceStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class InvoiceApiV1DtoTest {

  @Test
  void invoiceItem_shouldCalculateTotalsCorrectly() {
    InvoiceItem item = ImmutableInvoiceItem.builder()
        .itemName("Oil Change")
        .itemCode("SRV-001")
        .quantity(new BigDecimal("1"))
        .unitPrice(new BigDecimal("89.95"))
        .taxRate(new BigDecimal("0.07"))
        .build();

    assertEquals(new BigDecimal("89.95"), item.subTotal());
    assertEquals(new BigDecimal("6.30"), item.taxTotal()); // 89.95 * 0.07 = 6.30
    assertEquals(new BigDecimal("96.25"), item.total());
  }

  @Test
  void invoiceItem_shouldCalculateZeroTaxCorrectly() {
    InvoiceItem item = ImmutableInvoiceItem.builder()
        .itemName("Diagnostic")
        .itemCode("SRV-002")
        .quantity(BigDecimal.ONE)
        .unitPrice(new BigDecimal("50.00"))
        .taxRate(BigDecimal.ZERO)
        .build();

    assertEquals(new BigDecimal("50.00"), item.subTotal());
    assertEquals(new BigDecimal("0.00"), item.taxTotal());
    assertEquals(new BigDecimal("50.00"), item.total());
  }

  @Test
  void invoiceItem_shouldCalculateTotalsWithFractionalQuantity() {
    InvoiceItem item = ImmutableInvoiceItem.builder()
        .itemName("Oil Mobile-5w40")
        .itemCode("PRT-001")
        .quantity(new BigDecimal("0.5"))
        .unitPrice(new BigDecimal("18.00"))
        .taxRate(new BigDecimal("0.07"))
        .build();

    assertEquals(new BigDecimal("9.00"), item.subTotal()); // 0.5 * 18.00 = 9.00
    assertEquals(new BigDecimal("0.63"), item.taxTotal()); // 9.00 * 0.07
    assertEquals(new BigDecimal("9.63"), item.total());
  }

  @Test
  void invoiceItem_shouldCalculateTotalsWithMultipleQuantity() {
    InvoiceItem item = ImmutableInvoiceItem.builder()
        .itemName("Brake Pad Set")
        .itemCode("PRT-002")
        .quantity(new BigDecimal("3.25"))
        .unitPrice(new BigDecimal("45.50"))
        .taxRate(new BigDecimal("0.05"))
        .build();

    assertEquals(new BigDecimal("147.88"), item.subTotal()); // 3.25 * 45.50 = 147.88
    assertEquals(new BigDecimal("7.39"), item.taxTotal()); // 147.88 * 0.05 = 7.39
    assertEquals(new BigDecimal("155.27"), item.total());
  }

  @Test
  void invoiceItem_shouldHandleZeroPrice() {
    InvoiceItem item = ImmutableInvoiceItem.builder()
        .itemName("Wash")
        .itemCode("SRV-003")
        .quantity(BigDecimal.ONE)
        .unitPrice(BigDecimal.ZERO)
        .taxRate(new BigDecimal("0.07"))
        .build();

    assertEquals(new BigDecimal("0.00"), item.subTotal());
    assertEquals(new BigDecimal("0.00"), item.taxTotal());
    assertEquals(new BigDecimal("0.00"), item.total());
  }

  @Test
  void invoiceItem_shouldHandleZeroTaxRate() {
    InvoiceItem item = ImmutableInvoiceItem.builder()
        .itemName("Alignment Check")
        .itemCode("SRV-004")
        .quantity(new BigDecimal("2"))
        .unitPrice(new BigDecimal("25.00"))
        .taxRate(BigDecimal.ZERO)
        .build();

    assertEquals(new BigDecimal("50.00"), item.subTotal());
    assertEquals(new BigDecimal("0.00"), item.taxTotal());
    assertEquals(new BigDecimal("50.00"), item.total());
  }

  @Test
  void invoice_shouldCalculateTotalAmountFromItems() {
    InvoiceItem labor = ImmutableInvoiceItem.builder()
        .itemName("Labor - Engine Repair")
        .itemCode("LAB-001")
        .quantity(BigDecimal.ONE)
        .unitPrice(new BigDecimal("300.00"))
        .taxRate(new BigDecimal("0.05"))
        .build();

    InvoiceItem parts = ImmutableInvoiceItem.builder()
        .itemName("Replacement Parts")
        .itemCode("PRT-010")
        .quantity(new BigDecimal("2"))
        .unitPrice(new BigDecimal("125.00"))
        .taxRate(new BigDecimal("0.05"))
        .build();

    Invoice invoice = ImmutableInvoice.builder()
        .invoiceNumber("UNIT-TEST-" + UUID.randomUUID())
        .version(1)
        .status(InvoiceStatus.DRAFT)
        .customerId("CUST-001")
        .addItems(labor, parts)
        .issueDate(LocalDate.now())
        .dueDate(LocalDate.now().plusDays(7))
        .createdAt(Instant.now())
        .createdBy("tester")
        .updatedAt(Instant.now())
        .updatedBy("tester")
        .build();

    BigDecimal expectedTotal = labor.total().add(parts.total());
    assertEquals(expectedTotal, invoice.totalAmount());
  }

  @Test
  void invoice_shouldDetectOverdueCorrectly() {
    Invoice invoice = ImmutableInvoice.builder()
        .invoiceNumber("UNIT-TEST-" + UUID.randomUUID())
        .version(1)
        .status(InvoiceStatus.ISSUED)
        .customerId("CUST-002")
        .issueDate(LocalDate.now().minusDays(10))
        .dueDate(LocalDate.now().minusDays(1))
        .createdAt(Instant.now())
        .createdBy("tester")
        .updatedAt(Instant.now())
        .updatedBy("tester")
        .build();

    assertTrue(invoice.isOverdue());
  }

  @Test
  void invoice_shouldReturnFalseWhenNoDueDate() {
    Invoice invoice = ImmutableInvoice.builder()
        .invoiceNumber("UNIT-TEST-" + UUID.randomUUID())
        .version(1)
        .status(InvoiceStatus.ISSUED)
        .customerId("CUST-003")
        .issueDate(LocalDate.now())
        .createdAt(Instant.now())
        .createdBy("tester")
        .updatedAt(Instant.now())
        .updatedBy("tester")
        .build();

    assertFalse(invoice.isOverdue());
  }
}
