package com.interview.integration;

import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.CONFLICT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.ErrorResponse;
import com.interview.invoices.api.v1.ImmutableCreateInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableUpdateInvoiceRequest;
import com.interview.invoices.api.v1.Invoice;
import com.interview.invoices.api.v1.UpdateInvoiceRequest;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class InvoiceApiUpdateInvoiceTest extends AbstractInvoiceApiIntegrationTest {

  @Test
  void updateInvoice_shouldUpdateDraftInvoiceSuccessfully() throws Exception {
    String invoiceNumber = "UPDATE-" + UUID.randomUUID();
    String customerId = UUID.randomUUID().toString();

    CreateInvoiceRequest createRequest = ImmutableCreateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .customerId(customerId)
        .notes("Initial Notes")
        .paymentInstructions("Initial Payment")
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

    UpdateInvoiceRequest updateRequest = ImmutableUpdateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(createdInvoice.version())
        .notes("Updated Notes")
        .paymentInstructions(createdInvoice.paymentInstructions())
        .issueDate(createdInvoice.issueDate())
        .dueDate(createdInvoice.dueDate())
        .updatedBy("int-tester")
        .build();

    String updateResponseJson = mockMvc.perform(put(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice updatedInvoice = parseJson(updateResponseJson, Invoice.class);

    assertEquals(invoiceNumber, updatedInvoice.invoiceNumber());
    assertEquals("Updated Notes", updatedInvoice.notes().get());
    assertEquals(createdInvoice.version() + 1, updatedInvoice.version());
  }

  @Test
  void updateInvoice_shouldFailOnVersionMismatch() throws Exception {
    String invoiceNumber = "UPDATE-LOCK-" + UUID.randomUUID();
    String customerId = UUID.randomUUID().toString();

    CreateInvoiceRequest createRequest = ImmutableCreateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .customerId(customerId)
        .notes("Notes")
        .paymentInstructions("Payment")
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

    UpdateInvoiceRequest updateRequest = ImmutableUpdateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(createdInvoice.version() + 1)
        .notes("Should fail")
        .issueDate(LocalDate.now())
        .updatedBy("int-tester")
        .build();

    String responseJson = mockMvc.perform(put(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateRequest)))
        .andExpect(status().isConflict())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ErrorResponse actualError = parseJson(responseJson, ErrorResponse.class);
    assertEquals(CONFLICT.name(), actualError.code());
  }

}
