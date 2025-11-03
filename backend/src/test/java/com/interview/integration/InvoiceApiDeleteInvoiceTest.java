package com.interview.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.DeleteInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableCreateInvoiceRequest;
import com.interview.invoices.api.v1.ImmutableDeleteInvoiceRequest;
import com.interview.invoices.api.v1.Invoice;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class InvoiceApiDeleteInvoiceTest extends AbstractInvoiceApiIntegrationTest {

  @Test
  void deleteInvoice_shouldRemoveDraftInvoiceSuccessfully() throws Exception {
    String invoiceNumber = "DELETE-" + UUID.randomUUID();
    String customerId = UUID.randomUUID().toString();

    CreateInvoiceRequest createRequest = ImmutableCreateInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .customerId(customerId)
        .notes("Delete test")
        .paymentInstructions("Payment info")
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

    DeleteInvoiceRequest deleteRequest = ImmutableDeleteInvoiceRequest.builder()
        .invoiceNumber(invoiceNumber)
        .version(createdInvoice.version())
        .build();

    mockMvc.perform(delete(INVOICE_API_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(deleteRequest)))
        .andExpect(status().isNoContent());

    mockMvc.perform(get(INVOICE_API_BASE_URL + "/" + invoiceNumber))
        .andExpect(status().isNotFound());
  }

}
