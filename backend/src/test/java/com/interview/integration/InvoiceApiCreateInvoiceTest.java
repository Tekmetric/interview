package com.interview.integration;

import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.ENTITY_ALREADY_EXISTS;
import static com.interview.invoices.api.v1.InvoiceStatus.DRAFT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.ErrorResponse;
import com.interview.invoices.api.v1.ImmutableCreateInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableInvoice;
import com.interview.invoices.api.v1.Invoice;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class InvoiceApiCreateInvoiceTest extends AbstractInvoiceApiIntegrationTest {

  @Test
  void createInvoice_shouldReturnCreatedStatusAndInvoiceData() throws Exception {
    String invoiceNumber = "TEST-" + UUID.randomUUID();

    Invoice templateInvoice = ImmutableInvoice.builder()
        .invoiceNumber(invoiceNumber)
        .version(0)
        .status(DRAFT)
        .customerId(UUID.randomUUID().toString())
        .notes("Notes")
        .paymentInstructions("Payment instructions")
        .issueDate(LocalDate.now())
        .dueDate(LocalDate.now().plusDays(7))
        .createdBy("int-tester")
        .createdAt(Instant.now())
        .updatedBy("int-tester")
        .updatedAt(Instant.now())
        .build();

    CreateInvoiceRequest request = ImmutableCreateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .customerId(templateInvoice.customerId())
        .notes(templateInvoice.notes())
        .paymentInstructions(templateInvoice.paymentInstructions())
        .issueDate(templateInvoice.issueDate())
        .dueDate(templateInvoice.dueDate())
        .createdBy(templateInvoice.createdBy())
        .build();

    String jsonResponse = mockMvc.perform(post(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice actualResponse = parseJson(jsonResponse, Invoice.class);

    Invoice expectedInvoice = ImmutableInvoice.builder()
        .from(templateInvoice)
        .createdAt(actualResponse.createdAt())
        .updatedAt(actualResponse.updatedAt())
        .build();

    assertEquals(expectedInvoice, actualResponse);
  }

  @Test
  void createInvoice_duplicateNumber_shouldReturnConflict() throws Exception {
    String invoiceNumber = "TEST-" + UUID.randomUUID();
    String customerId = UUID.randomUUID().toString();

    CreateInvoiceRequest request = ImmutableCreateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .customerId(customerId)
        .notes("Notes")
        .paymentInstructions("Payment instructions")
        .issueDate(LocalDate.now())
        .dueDate(LocalDate.now().plusDays(7))
        .createdBy("int-tester")
        .build();

    mockMvc.perform(post(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)))
        .andExpect(status().isCreated());

    String responseJson = mockMvc.perform(post(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)))
        .andExpect(status().isBadRequest())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ErrorResponse actualError = parseJson(responseJson, ErrorResponse.class);
    assertEquals(ENTITY_ALREADY_EXISTS.name(), actualError.code());
  }
}
