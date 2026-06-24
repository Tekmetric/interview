package com.interview.invoices.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
    name = "Invoice Management API (v1)",
    description = "Provides CRUD, search and lifecycle management operations for invoices.")
@RequestMapping(path = "/v1/invoices")
public interface InvoiceMakerApi {

  @Operation(
      summary = "Get invoice by number",
      description = "Retrieves full invoice details for the specified invoice number.")
  @GetMapping("/{invoiceNumber}")
  ResponseEntity<Invoice> getInvoiceByNumber(@Parameter(description = "Invoice number") @PathVariable("invoiceNumber") String invoiceNumber);

  @Operation(
      summary = "Find invoices",
      description = "Returns a list of invoices matching the specified filters with pagination options."
                    + " All parameters are optional and combined using the AND operator."
                    + " Supports filtering by invoice number (LIKE), customer ID, statuses, issue/due/paid date ranges,")
  @GetMapping
  ResponseEntity<FindInvoicesResponse> findInvoices(
      @Parameter(description = "Invoice number (partially match, case-insensitive)")
      @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,

      @Parameter(description = "Customer ID")
      @RequestParam(value = "customerId", required = false) String customerId,

      @Parameter(description = "List of invoice statuses to include")
      @RequestParam(value = "status", required = false) List<InvoiceStatus> statuses,

      @Parameter(description = "Issue date range start (inclusive)")
      @RequestParam(value = "issueDateFrom", required = false) LocalDate issueDateFrom,

      @Parameter(description = "Issue date range end (inclusive)")
      @RequestParam(value = "issueDateTo", required = false) LocalDate issueDateTo,

      @Parameter(description = "Due date range start (inclusive)")
      @RequestParam(value = "dueDateFrom", required = false) LocalDate dueDateFrom,

      @Parameter(description = "Due date range end (inclusive)")
      @RequestParam(value = "dueDateTo", required = false) LocalDate dueDateTo,

      @Parameter(description = "Paid date range start (inclusive)")
      @RequestParam(value = "paidDateFrom", required = false) LocalDate paidDateFrom,

      @Parameter(description = "Paid date range end (inclusive)")
      @RequestParam(value = "paidDateTo", required = false) LocalDate paidDateTo,

      @Parameter(description = "Result page number")
      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,

      @Parameter(description = "Maximum number of records per page")
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,

      @Parameter(description = "Sort field. Allowed fields: invoiceNumber, status, customerId, issueDate, dueDate, paidDate.")
      @RequestParam(value = "sortBy", required = false, defaultValue = "issueDate") InvoiceSortField sortBy,

      @Parameter(description = "Ordering. Allowed options: asc, desc.")
      @RequestParam(value = "order", required = false, defaultValue = "desc") String order
  );

  @Operation(
      summary = "Create a new invoice",
      description = "Creates a new invoice. Returns the created invoice.")
  @PostMapping
  ResponseEntity<Invoice> createInvoice(@Parameter(description = "New invoice info") @RequestBody CreateInvoiceRequest request);

  @Operation(
      summary = "Update an existing invoice",
      description = "Fully update an existing invoice identified by ID and version. Version used for optimistic lock.")
  @PutMapping
  ResponseEntity<Invoice> updateInvoice(@Parameter(description = "Updated invoice") @RequestBody UpdateInvoiceRequest request);

  @Operation(
      summary = "Delete an invoice",
      description = "Invoice identified by it's ID and version for optimistic lock. Only invoices in DRAFT status can be deleted.")
  @DeleteMapping
  ResponseEntity<Void> deleteInvoice(@Parameter(description = "Invoice to delete") @RequestBody DeleteInvoiceRequest request);

  @PutMapping("/issue")
  @Operation(
      summary = "Issue an invoice",
      description = "Issue the specified invoice. Only invoices in DRAFT status can be issued.")
  ResponseEntity<Invoice> issueInvoice(@Parameter(description = "Invoice to delete") @RequestBody IssueInvoiceRequest request);

  @PutMapping("/pay")
  @Operation(
      summary = "Mark invoice as paid",
      description = "Marks the specified invoice as PAID. Only invoices in ISSUED status can be marked as paid."
  )
  ResponseEntity<Invoice> payInvoice(@Parameter(description = "Payment confirmation request") @RequestBody PayInvoiceRequest request);

  @PutMapping("/cancel")
  @Operation(
      summary = "Cancel invoice",
      description = "Cancel the specified invoice. Only invoices in ISSUED status can be cancelled."
  )
  ResponseEntity<Invoice> cancelInvoice(@Parameter(description = "Cancel invoice request") @RequestBody CancelInvoiceRequest request);
}
