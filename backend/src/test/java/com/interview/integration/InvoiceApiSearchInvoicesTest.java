package com.interview.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.FindInvoicesResponse;
import com.interview.invoices.api.v1.ImmutableCreateInvoiceRequest;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class InvoiceApiSearchInvoicesTest extends AbstractInvoiceApiIntegrationTest {

  @Test
  void findInvoices_shouldReturnInvoicesByPartialNumber_withDefaultPagingAndSorting() throws Exception {
    String basePrefix = "SEARCH-" + UUID.randomUUID();

    for (int i = 1; i <= 3; i++) {
      CreateInvoiceRequest request = ImmutableCreateInvoiceRequest.builder()
          .invoiceNumber(basePrefix + "-" + i)
          .customerId(UUID.randomUUID().toString())
          .notes("Notes " + i)
          .paymentInstructions("Payment " + i)
          .issueDate(LocalDate.now().minusDays(i))
          .dueDate(LocalDate.now().plusDays(i))
          .createdBy("int-tester")
          .build();

      mockMvc.perform(post(INVOICE_API_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(toJson(request)))
          .andExpect(status().isCreated());
    }

    String responseJson = mockMvc.perform(get(INVOICE_API_BASE_URL)
            .param("invoiceNumber", basePrefix)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    FindInvoicesResponse response = parseJson(responseJson, FindInvoicesResponse.class);

    assertEquals(3, response.invoices().size());
    assertEquals(basePrefix + "-1", response.invoices().get(0).invoiceNumber());
  }

  @Test
  void findInvoices_shouldRespectCustomPaginationAndSorting() throws Exception {
    String basePrefix = "SORT-" + UUID.randomUUID();

    for (int i = 1; i <= 5; i++) {
      CreateInvoiceRequest request = ImmutableCreateInvoiceRequest.builder()
          .invoiceNumber(basePrefix + "-" + i)
          .customerId(UUID.randomUUID().toString())
          .notes("Notes " + i)
          .paymentInstructions("Payment " + i)
          .issueDate(LocalDate.now().minusDays(i))
          .dueDate(LocalDate.now().plusDays(i))
          .createdBy("int-tester")
          .build();

      mockMvc.perform(post(INVOICE_API_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(toJson(request)))
          .andExpect(status().isCreated());
    }

    String responseJson = mockMvc.perform(get(INVOICE_API_BASE_URL)
            .param("invoiceNumber", basePrefix)
            .param("pageNumber", "1")
            .param("pageSize", "2")
            .param("sortBy", "invoiceNumber")
            .param("order", "asc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    FindInvoicesResponse response = parseJson(responseJson, FindInvoicesResponse.class);

    assertEquals(2, response.invoices().size());
    assertEquals(basePrefix + "-3", response.invoices().get(0).invoiceNumber());
    assertEquals(basePrefix + "-4", response.invoices().get(1).invoiceNumber());
  }
}
