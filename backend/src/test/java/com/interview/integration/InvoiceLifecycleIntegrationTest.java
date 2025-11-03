package com.interview.integration;

import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.OPERATION_NOT_ALLOWED;
import static com.interview.invoices.api.v1.InvoiceStatus.DRAFT;
import static com.interview.invoices.api.v1.InvoiceStatus.ISSUED;
import static com.interview.invoices.api.v1.InvoiceStatus.PAID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.invoices.api.v1.CancelInvoiceRequest;
import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.DeleteInvoiceRequest;
import com.interview.invoices.api.v1.ErrorResponse;
import com.interview.invoices.api.v1.ImmutableCancelInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableCreateInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableDeleteInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableInvoiceItem;
import com.interview.invoices.api.v1.ImmutableIssueInvoiceRequest;
import com.interview.invoices.api.v1.ImmutablePayInvoiceRequest;
import com.interview.invoices.api.v1.Invoice;
import com.interview.invoices.api.v1.InvoiceItem;
import com.interview.invoices.api.v1.IssueInvoiceRequest;
import com.interview.invoices.api.v1.PayInvoiceRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class InvoiceLifecycleIntegrationTest extends AbstractInvoiceApiIntegrationTest {

  @Test
  void fullInvoiceLifecycle_shouldFollowValidStateTransitions() throws Exception {
    String invoiceNumber = "LIFECYCLE-" + UUID.randomUUID();
    String customerId = UUID.randomUUID().toString();

    InvoiceItem items = ImmutableInvoiceItem.builder()
        .itemName("Replacement Parts")
        .itemCode("PRT-010")
        .quantity(new BigDecimal("2"))
        .unitPrice(new BigDecimal("125.00"))
        .taxRate(new BigDecimal("0.05"))
        .build();

    CreateInvoiceRequest createRequest = ImmutableCreateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .customerId(customerId)
        .addItems(items)
        .notes("Initial draft")
        .paymentInstructions("Wire transfer")
        .issueDate(LocalDate.now())
        .dueDate(LocalDate.now().plusDays(7))
        .createdBy("int-tester")
        .build();

    String creationResponse = mockMvc.perform(post(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice createdInvoice = parseJson(creationResponse, Invoice.class);
    assertEquals(DRAFT, createdInvoice.status());

    // Try to pay DRAFT - should fail
    PayInvoiceRequest payDraftRequest = ImmutablePayInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(createdInvoice.version())
        .transactionReference("attempt-before-issue")
        .build();

    String payDraftResponse = mockMvc.perform(put(INVOICE_API_BASE_URL + "/pay")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(payDraftRequest)))
        .andExpect(status().isForbidden())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ErrorResponse payDraftError = parseJson(payDraftResponse, ErrorResponse.class);
    assertEquals(OPERATION_NOT_ALLOWED.name(), payDraftError.code());

    // Try to cancel DRAFT - should fail
    CancelInvoiceRequest cancelDraftRequest = ImmutableCancelInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(createdInvoice.version())
        .cancelledBy("int-tester")
        .comment("cancel before issue")
        .build();

    String cancelDraftResponse = mockMvc.perform(put(INVOICE_API_BASE_URL + "/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(cancelDraftRequest)))
        .andExpect(status().isForbidden())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ErrorResponse cancelDraftError = parseJson(cancelDraftResponse, ErrorResponse.class);
    assertEquals(OPERATION_NOT_ALLOWED.name(), cancelDraftError.code());

    // Issue invoice
    IssueInvoiceRequest issueRequest = ImmutableIssueInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(createdInvoice.version())
        .issuedBy("int-tester")
        .build();

    String issueResponse = mockMvc.perform(put(INVOICE_API_BASE_URL + "/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(issueRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice issuedInvoice = parseJson(issueResponse, Invoice.class);
    assertEquals(ISSUED, issuedInvoice.status());
    assertEquals(createdInvoice.version() + 1, issuedInvoice.version());

    // Try to delete ISSUED - should fail
    DeleteInvoiceRequest deleteIssuedRequest = ImmutableDeleteInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(issuedInvoice.version())
        .build();

    String deleteIssuedResponse = mockMvc.perform(delete(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(deleteIssuedRequest)))
        .andExpect(status().isForbidden())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ErrorResponse deleteIssuedError = parseJson(deleteIssuedResponse, ErrorResponse.class);
    assertEquals(OPERATION_NOT_ALLOWED.name(), deleteIssuedError.code());

    // Pay invoice
    PayInvoiceRequest payRequest = ImmutablePayInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(issuedInvoice.version())
        .transactionReference("TXN-123")
        .build();

    String payResponse = mockMvc.perform(put(INVOICE_API_BASE_URL + "/pay")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(payRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice paidInvoice = parseJson(payResponse, Invoice.class);
    assertEquals(PAID, paidInvoice.status());
    assertNotNull(paidInvoice.paidDate());
    assertEquals(issuedInvoice.version() + 1, paidInvoice.version());


    String getResponse = mockMvc.perform(get(INVOICE_API_BASE_URL + "/" + invoiceNumber))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice fetchedInvoice = parseJson(getResponse, Invoice.class);
    assertEquals(PAID, fetchedInvoice.status());
    assertEquals(paidInvoice.version(), fetchedInvoice.version());
  }
}
