package com.interview.invoices.endpoints;

import com.interview.invoices.domain.CustomerInvoice;
import com.interview.invoices.domain.CustomerInvoiceItem;
import com.interview.invoices.domain.ImmutableInvoiceSearchParameters;
import com.interview.invoices.domain.InvoiceSearchParameters;
import com.interview.invoices.domain.InvoiceSearchResult;
import com.interview.invoices.services.InvoiceService;
import com.interview.invoices.api.v1.CancelInvoiceRequest;
import com.interview.invoices.api.v1.CreateInvoiceRequest;
import com.interview.invoices.api.v1.DeleteInvoiceRequest;
import com.interview.invoices.api.v1.FindInvoicesResponse;
import com.interview.invoices.api.v1.ImmutableFindInvoicesResponse;
import com.interview.invoices.api.v1.ImmutableInvoice;
import com.interview.invoices.api.v1.ImmutableInvoiceItem;
import com.interview.invoices.api.v1.Invoice;
import com.interview.invoices.api.v1.InvoiceItem;
import com.interview.invoices.api.v1.InvoiceMakerApi;
import com.interview.invoices.api.v1.InvoiceSortField;
import com.interview.invoices.api.v1.InvoiceStatus;
import com.interview.invoices.api.v1.IssueInvoiceRequest;
import com.interview.invoices.api.v1.PayInvoiceRequest;
import com.interview.invoices.api.v1.UpdateInvoiceRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceMakerResource implements InvoiceMakerApi {

  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceMakerResource(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @Override
  public ResponseEntity<Invoice> getInvoiceByNumber(String invoiceNumber) {
    CustomerInvoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);

    Invoice response = invoiceApiDtoFrom(invoice);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<FindInvoicesResponse> findInvoices(
      String invoiceNumber,
      String customerId,
      List<InvoiceStatus> statuses,
      LocalDate issueDateFrom,
      LocalDate issueDateTo,
      LocalDate dueDateFrom,
      LocalDate dueDateTo,
      LocalDate paidDateFrom,
      LocalDate paidDateTo,
      int pageNumber,
      int pageSize,
      InvoiceSortField sortBy,
      String order) {

    List<CustomerInvoice.Status> parsedStatuses = Optional.ofNullable(statuses)
        .orElseGet(ArrayList::new)
        .stream()
        .map(status -> CustomerInvoice.Status.valueOf(status.name()))
        .collect(Collectors.toList());

    InvoiceSearchParameters searchParameters = ImmutableInvoiceSearchParameters.builder()
        .invoiceNumber(Optional.ofNullable(invoiceNumber))
        .customerId(Optional.ofNullable(customerId))
        .statuses(parsedStatuses)
        .issueDateFrom(Optional.ofNullable(issueDateFrom))
        .issueDateTo(Optional.ofNullable(issueDateTo))
        .dueDateFrom(Optional.ofNullable(dueDateFrom))
        .dueDateTo(Optional.ofNullable(dueDateTo))
        .paidDateFrom(Optional.ofNullable(paidDateFrom))
        .paidDateTo(Optional.ofNullable(paidDateTo))
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .sortBy(InvoiceSearchParameters.SortField.valueOf(sortBy.name()))
        .orderBy(InvoiceSearchParameters.SortOrder.valueOf(order))
        .build();

    InvoiceSearchResult searchResult = invoiceService.findInvoices(searchParameters);

    List<Invoice> invoices = searchResult.invoices()
        .stream()
        .map(InvoiceMakerResource::invoiceApiDtoFrom)
        .collect(Collectors.toList());

    FindInvoicesResponse response = ImmutableFindInvoicesResponse.builder()
        .invoices(invoices)
        .pageNumber(searchResult.pageNumber())
        .pageSize(searchResult.pageSize())
        .totalPages(searchResult.totalPages())
        .sortBy(sortBy)
        .order(order)
        .build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Invoice> createInvoice(CreateInvoiceRequest request) {
    List<CustomerInvoiceItem> invoiceItems = customerInvoiceItemsFrom(request.items());

    CustomerInvoice createdInvoice = invoiceService.createInvoice(
        request.invoiceNumber(),
        request.customerId(),
        request.notes().orElse(null),
        request.paymentInstructions().orElse(null),
        request.issueDate(),
        request.dueDate().orElse(null),
        request.createdBy(),
        invoiceItems);

    Invoice response = invoiceApiDtoFrom(createdInvoice);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  public ResponseEntity<Invoice> updateInvoice(UpdateInvoiceRequest request) {
    List<CustomerInvoiceItem> invoiceItems = customerInvoiceItemsFrom(request.items());

    CustomerInvoice updatedInvoice = invoiceService.updateInvoice(
        request.invoiceNumber(),
        request.version(),
        request.notes().orElse(null),
        request.paymentInstructions().orElse(null),
        request.issueDate(),
        request.dueDate().orElse(null),
        request.updatedBy(),
        invoiceItems);

    Invoice response = invoiceApiDtoFrom(updatedInvoice);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> deleteInvoice(DeleteInvoiceRequest request) {
    invoiceService.deleteInvoice(request.invoiceNumber(), request.version());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<Invoice> issueInvoice(IssueInvoiceRequest request) {
    CustomerInvoice issuedInvoice = invoiceService.issueInvoice(request.invoiceNumber(), request.version(), request.issuedBy());
    return ResponseEntity.ok(invoiceApiDtoFrom(issuedInvoice));
  }

  @Override
  public ResponseEntity<Invoice> payInvoice(PayInvoiceRequest request) {
    CustomerInvoice issuedInvoice = invoiceService.payInvoice(request.invoiceNumber(), request.version(), request.transactionReference());
    return ResponseEntity.ok(invoiceApiDtoFrom(issuedInvoice));
  }

  @Override
  public ResponseEntity<Invoice> cancelInvoice(CancelInvoiceRequest request) {
    CustomerInvoice cancelledInvoice = invoiceService.cancelInvoice(
        request.invoiceNumber(),
        request.version(),
        request.cancelledBy(),
        request.comment());
    return ResponseEntity.ok(invoiceApiDtoFrom(cancelledInvoice));
  }

  private static List<CustomerInvoiceItem> customerInvoiceItemsFrom(List<InvoiceItem> apiDtoItems) {
    return apiDtoItems
        .stream()
        .map(InvoiceMakerResource::customerInvoiceItemFrom)
        .collect(Collectors.toList());
  }

  private static CustomerInvoiceItem customerInvoiceItemFrom(InvoiceItem apiItemDto) {
    CustomerInvoiceItem invoiceItem = new CustomerInvoiceItem();
    invoiceItem.setItemCode(apiItemDto.itemCode());
    invoiceItem.setItemName(apiItemDto.itemName());
    invoiceItem.setQuantity(apiItemDto.quantity());
    invoiceItem.setUnitPrice(apiItemDto.unitPrice());
    invoiceItem.setTaxRate(apiItemDto.taxRate());
    return invoiceItem;
  }

  private static InvoiceItem invoiceItemApiDtoFrom(CustomerInvoiceItem item) {
    return ImmutableInvoiceItem.builder()
        .itemCode(item.getItemCode())
        .itemName(item.getItemName())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .taxRate(item.getTaxRate())
        .build();
  }

  private static Invoice invoiceApiDtoFrom(CustomerInvoice invoice) {
    List<InvoiceItem> items = invoice.getItems()
        .stream()
        .map(InvoiceMakerResource::invoiceItemApiDtoFrom)
        .collect(Collectors.toList());

    return ImmutableInvoice.builder()
        .invoiceNumber(invoice.getInvoiceNumber())
        .version(invoice.getVersion())
        .status(InvoiceStatus.valueOf(invoice.getStatus().name()))
        .customerId(invoice.getCustomerId())
        .items(items)
        .notes(Optional.ofNullable(invoice.getNotes()))
        .paymentInstructions(Optional.ofNullable(invoice.getPaymentInstructions()))
        .issueDate(invoice.getIssueDate())
        .dueDate(Optional.ofNullable(invoice.getDueDate()))
        .paidDate(Optional.ofNullable(invoice.getPaidDate()))
        .createdAt(invoice.getCreatedAt())
        .createdBy(invoice.getCreatedBy())
        .updatedAt(invoice.getUpdatedAt())
        .updatedBy(invoice.getUpdatedBy())
        .build();
  }
}
