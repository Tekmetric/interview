package com.interview.invoices.services;

import static com.interview.invoices.domain.CustomerInvoice.Status.DRAFT;
import static com.interview.invoices.domain.CustomerInvoice.Status.ISSUED;
import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.CONFLICT;
import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.ENTITY_ALREADY_EXISTS;
import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.ENTITY_NOT_FOUND;
import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.OPERATION_NOT_ALLOWED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.interview.invoices.domain.CustomerInvoice;
import com.interview.invoices.domain.CustomerInvoiceItem;
import com.interview.invoices.domain.ImmutableInvoiceSearchResult;
import com.interview.invoices.domain.InvoiceSearchParameters;
import com.interview.invoices.domain.InvoiceSearchResult;
import com.interview.invoices.repository.CustomerInvoiceRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
  private static final Logger LOG = LoggerFactory.getLogger(InvoiceService.class);

  private final CustomerInvoiceRepository invoiceRepository;

  @Autowired
  public InvoiceService(CustomerInvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  public CustomerInvoice getInvoiceByNumber(String invoiceNumber) {
    return invoiceRepository.findById(invoiceNumber)
        .orElseThrow(() -> new InvoiceServiceException(ENTITY_NOT_FOUND, "Invoice # '%s' not found.", invoiceNumber));
  }
  public InvoiceSearchResult findInvoices(InvoiceSearchParameters searchParameters) {
    Specification<CustomerInvoice> spec = searchParameters.repositorySpecification();
    Pageable pageable = searchParameters.pageableRequest();

    Page<CustomerInvoice> searchResult = invoiceRepository.findAll(spec, pageable);

    return ImmutableInvoiceSearchResult.builder()
        .invoices(searchResult.getContent())
        .pageNumber(searchParameters.pageNumber())
        .pageSize(searchParameters.pageSize())
        .totalPages(searchResult.getTotalPages())
        .build();
  }

  @Transactional
  public CustomerInvoice createInvoice(
      String invoiceNumber,
      String customerId,
      String notes,
      String paymentInstructions,
      LocalDate issueDate,
      LocalDate dueDate,
      String createdBy,
      List<CustomerInvoiceItem> invoiceItems) {
    Instant now = Instant.now();

    CustomerInvoice newInvoice = new CustomerInvoice();
    newInvoice.setInvoiceNumber(invoiceNumber);
    newInvoice.setStatus(DRAFT);
    newInvoice.setCustomerId(customerId);
    newInvoice.setItems(invoiceItems);
    newInvoice.setIssueDate(issueDate);
    newInvoice.setCreatedAt(now);
    newInvoice.setCreatedBy(createdBy);
    newInvoice.setUpdatedAt(now);
    newInvoice.setUpdatedBy(createdBy);
    if (nonNull(notes)) {
      newInvoice.setNotes(notes);
    }
    if (nonNull(paymentInstructions)) {
      newInvoice.setPaymentInstructions(paymentInstructions);
    }
    if (nonNull(dueDate)) {
      newInvoice.setDueDate(dueDate);
    }

    try {
      LOG.debug("Persisting invoices # '{}' ...", invoiceNumber);
      return invoiceRepository.saveAndFlush(newInvoice);
    } catch (DataIntegrityViolationException e) {
      throw new InvoiceServiceException(ENTITY_ALREADY_EXISTS, "Invoice with number '%s' already exists.", invoiceNumber);
    }
  }

  @Transactional
  public CustomerInvoice updateInvoice(
      String invoiceNumber,
      int currentVersion,
      String notes,
      String paymentInstructions,
      LocalDate issueDate,
      LocalDate dueDate,
      String updatedBy,
      List<CustomerInvoiceItem> invoiceItems) {
    CustomerInvoice invoice = getDraftInvoiceOrThrow(invoiceNumber, currentVersion);

    invoice.setNotes(notes);
    invoice.setPaymentInstructions(paymentInstructions);
    invoice.setIssueDate(issueDate);
    invoice.setDueDate(dueDate);
    invoice.setItems(invoiceItems);
    invoice.setUpdatedBy(updatedBy);
    invoice.setUpdatedAt(Instant.now());

    try {
      LOG.debug("Updating invoices # '{}' ...", invoiceNumber);
      return invoiceRepository.saveAndFlush(invoice);
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }
  }

  @Transactional
  public void deleteInvoice(String invoiceNumber, int currentVersion) {
    CustomerInvoice invoice = getDraftInvoiceOrThrow(invoiceNumber, currentVersion);
    try {
      LOG.info("Deleting invoice # '{}' ...", invoiceNumber);
      invoiceRepository.delete(invoice);
      invoiceRepository.flush();
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was modified or deleted concurrently.", invoiceNumber);
    }
  }

  @Transactional
  public CustomerInvoice issueInvoice(String invoiceNumber, int currentVersion, String issuedBy) {
    CustomerInvoice invoice = getDraftInvoiceOrThrow(invoiceNumber, currentVersion);

    if (invoice.getStatus() != DRAFT) {
      throw new InvoiceServiceException(OPERATION_NOT_ALLOWED, "Only draft invoice can be issued.");
    }
    if (isNull(invoice.getCustomerId()) || invoice.getCustomerId().length() < 1) {
      throw new InvoiceServiceException(OPERATION_NOT_ALLOWED, "Customer ID must be specified.");
    }
    if (isNull(invoice.getDueDate())) {
      throw new InvoiceServiceException(OPERATION_NOT_ALLOWED, "Due date must be specified.");
    }
    if (isNull(invoice.getItems()) || invoice.getItems().isEmpty()) {
      throw new InvoiceServiceException(OPERATION_NOT_ALLOWED, "Invoice must contain at least one item.");
    }
    // Other required business logic (validations, customer notifications, etc).

    invoice.setStatus(CustomerInvoice.Status.ISSUED);
    invoice.setUpdatedAt(Instant.now());
    invoice.setUpdatedBy(issuedBy);

    try {
      LOG.debug("Issuing invoice # '{}' ...", invoiceNumber);
      return invoiceRepository.saveAndFlush(invoice);
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }
  }

  @Transactional
  public CustomerInvoice payInvoice(String invoiceNumber, int currentVersion, String transactionReference) {
    CustomerInvoice invoice = getInvoiceByNumber(invoiceNumber);
    if (!invoice.getVersion().equals(currentVersion)) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }
    if (invoice.getStatus() != ISSUED) {
      throw new InvoiceServiceException(OPERATION_NOT_ALLOWED, "Only issued invoice can be paid.");
    }

    invoice.setStatus(CustomerInvoice.Status.PAID);
    invoice.setPaidDate(LocalDate.now());
    invoice.setUpdatedAt(Instant.now());
    // Other required business logic (validations, customer notifications, save transaction reference, check partially/full payment, etc).

    try {
      LOG.debug("Marking invoice # '{}' as PAID ...", invoiceNumber);
      return invoiceRepository.saveAndFlush(invoice);
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }
  }

  @Transactional
  public CustomerInvoice cancelInvoice(String invoiceNumber, int currentVersion, String cancelledBy, String comment) {
    CustomerInvoice invoice = getInvoiceByNumber(invoiceNumber);

    if (!invoice.getVersion().equals(currentVersion)) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }
    if (invoice.getStatus() != CustomerInvoice.Status.ISSUED) {
      throw new InvoiceServiceException(OPERATION_NOT_ALLOWED, "Only issued invoice can be cancelled.");
    }
    // Other required business logic (validations, customer notifications, etc).

    invoice.setStatus(CustomerInvoice.Status.CANCELLED);
    invoice.setNotes(
        (invoice.getNotes() == null ? "" : invoice.getNotes())
        + "\n=====\nCancellation notes:\n" + comment);
    invoice.setUpdatedAt(Instant.now());
    invoice.setUpdatedBy(cancelledBy);

    try {
      LOG.info("Cancelling invoice # '{}' ...", invoiceNumber);
      return invoiceRepository.saveAndFlush(invoice);
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }
  }

  private CustomerInvoice getDraftInvoiceOrThrow(String invoiceNumber, int version) {
    CustomerInvoice invoice = getInvoiceByNumber(invoiceNumber);

    if (!invoice.getVersion().equals(version)) {
      throw new InvoiceServiceException(CONFLICT, "Invoice '%s' was updated concurrently.", invoiceNumber);
    }

    if (invoice.getStatus() != DRAFT) {
      throw new InvoiceServiceException(
          OPERATION_NOT_ALLOWED,
          "Only draft invoices can be modified or deleted. Current status: '%s'.",
          invoice.getStatus());
    }

    return invoice;
  }
}
