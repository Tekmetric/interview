package com.interview.integration;

import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.ENTITY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.ErrorResponse;
import com.interview.invoices.api.v1.ImmutableCreateInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableInvoice;
import com.interview.invoices.api.v1.Invoice;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class InvoiceApiGetInvoiceByNumberTest extends AbstractInvoiceApiIntegrationTest {

  @Test
  void getInvoiceByNumber_shouldReturnInvoice_whenExists() throws Exception {
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

    String creationResponse = mockMvc.perform(post(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice createdInvoice = parseJson(creationResponse, Invoice.class);

    String getResponse = mockMvc.perform(get(INVOICE_API_BASE_URL + "/" + invoiceNumber))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Invoice actualInvoice = parseJson(getResponse, Invoice.class);

    Invoice expectedInvoice = ImmutableInvoice.builder()
        .from(createdInvoice)
        .createdAt(actualInvoice.createdAt())
        .updatedAt(actualInvoice.updatedAt())
        .build();

    assertEquals(expectedInvoice, actualInvoice);
  }

  @Test
  void getInvoiceByNumber_shouldReturnNotFound_whenMissing() throws Exception {
    String missingInvoiceNumber = "TEST-MISSING-" + UUID.randomUUID();

    String responseJson = mockMvc.perform(get(INVOICE_API_BASE_URL + "/" + missingInvoiceNumber))
        .andExpect(status().isNotFound())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ErrorResponse actualError = parseJson(responseJson, ErrorResponse.class);

    assertEquals(ENTITY_NOT_FOUND.name(), actualError.code());
  }
}
