package com.interview.unit;

import static com.interview.invoices.domain.CustomerInvoice.Status.ISSUED;
import static com.interview.invoices.domain.CustomerInvoice.Status.PAID;
import static com.interview.invoices.services.InvoiceServiceException.ErrorCode.ENTITY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.interview.invoices.domain.CustomerInvoice;
import com.interview.invoices.services.InvoiceService;
import com.interview.invoices.services.InvoiceServiceException;
import com.interview.invoices.repository.CustomerInvoiceRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {
  @Mock
  private CustomerInvoiceRepository repository;

  @InjectMocks
  private InvoiceService service;

  @Test
  void getInvoiceByNumber_shouldThrow_whenInvoiceNotFound() {
    String invoiceNumber = "UNIT-TEST-" + UUID.randomUUID();
    when(repository.findById(invoiceNumber)).thenReturn(Optional.empty());

    InvoiceServiceException actual = assertThrows(
        InvoiceServiceException.class,
        () -> service.getInvoiceByNumber(invoiceNumber)
    );

    assertEquals(ENTITY_NOT_FOUND, actual.errorCode());
    String expectedMessage = String.format("Invoice # '%s' not found.", invoiceNumber);
    assertEquals(expectedMessage, actual.getMessage());
  }

  @Test
  void updateInvoice_shouldThrow_whenStatusNotDraft() {
    String invoiceNumber = "UNIT-TEST-" + UUID.randomUUID();
    int version = 1;

    CustomerInvoice existing = new CustomerInvoice();
    existing.setInvoiceNumber(invoiceNumber);
    existing.setVersion(version);
    existing.setStatus(ISSUED);
    when(repository.findById(invoiceNumber)).thenReturn(Optional.of(existing));

    InvoiceServiceException actualException = assertThrows(
        InvoiceServiceException.class,
        () -> service.updateInvoice(
            invoiceNumber,
            version,
            "notes",
            "instructions",
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            "updater",
            new ArrayList<>()));

    assertEquals(
        InvoiceServiceException.ErrorCode.OPERATION_NOT_ALLOWED,
        actualException.errorCode()
    );

    assertEquals(
        "Only draft invoices can be modified or deleted. Current status: 'ISSUED'.",
        actualException.getMessage()
    );
  }

  @Test
  void updateInvoice_shouldThrow_whenVersionMismatch() {
    String invoiceNumber = "UNIT-TEST-" + UUID.randomUUID();
    int currentVersion = 2;

    CustomerInvoice existing = new CustomerInvoice();
    existing.setInvoiceNumber(invoiceNumber);
    existing.setVersion(1);
    existing.setStatus(CustomerInvoice.Status.DRAFT);
    when(repository.findById(invoiceNumber)).thenReturn(Optional.of(existing));

    InvoiceServiceException actualException = assertThrows(
        InvoiceServiceException.class,
        () -> service.updateInvoice(
            invoiceNumber,
            currentVersion,
            "notes",
            "instructions",
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            "updater",
            new ArrayList<>())
    );

    assertEquals(
        InvoiceServiceException.ErrorCode.CONFLICT,
        actualException.errorCode()
    );

    assertEquals(
        String.format("Invoice '%s' was updated concurrently.", invoiceNumber),
        actualException.getMessage()
    );
  }

  @Test
  void deleteInvoice_shouldThrow_whenStatusNotDraft() {
    String invoiceNumber = "UNIT-TEST-" + UUID.randomUUID();
    int version = 1;

    CustomerInvoice existing = new CustomerInvoice();
    existing.setInvoiceNumber(invoiceNumber);
    existing.setVersion(version);
    existing.setStatus(PAID);
    when(repository.findById(invoiceNumber)).thenReturn(Optional.of(existing));

    InvoiceServiceException actualException = assertThrows(
        InvoiceServiceException.class,
        () -> service.deleteInvoice(invoiceNumber, version)
    );

    assertEquals(
        InvoiceServiceException.ErrorCode.OPERATION_NOT_ALLOWED,
        actualException.errorCode()
    );

    assertEquals(
        "Only draft invoices can be modified or deleted. Current status: 'PAID'.",
        actualException.getMessage()
    );
  }
}
