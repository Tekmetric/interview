package com.interview.invoices.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "customer_invoices")
public class CustomerInvoice {

  @Id
  @Column(name = "invoice_number", nullable = false, updatable = false)
  private String invoiceNumber;

  @Version
  @Column(nullable = false)
  // Use 'Integer' instead 'int', because Hibernate should set it automatically to handle optimistic lock correctly
  private Integer version;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(name = "customer_id", nullable = false)
  private String customerId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "customer_invoice_items",
      joinColumns = @JoinColumn(name = "invoice_number", nullable = false)
  )
  private List<CustomerInvoiceItem> items = new ArrayList<>();

  @Column(name = "notes")
  private String notes;
  @Column(name = "payment_instructions")
  private String paymentInstructions;

  @Column(name = "issue_date", nullable = false)
  private LocalDate issueDate;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "paid_date")
  private LocalDate paidDate;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_by", nullable = false)
  private String updatedBy;

  public enum Status {
    DRAFT, ISSUED, PAID, CANCELLED, ARCHIVED
  }

  public CustomerInvoice() {
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public List<CustomerInvoiceItem> getItems() {
    return items;
  }

  public void setItems(List<CustomerInvoiceItem> items) {
    this.items = items;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getPaymentInstructions() {
    return paymentInstructions;
  }

  public void setPaymentInstructions(String paymentInstructions) {
    this.paymentInstructions = paymentInstructions;
  }

  public LocalDate getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDate getPaidDate() {
    return paidDate;
  }

  public void setPaidDate(LocalDate paidDate) {
    this.paidDate = paidDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public String toString() {
    return "CustomerInvoice{" +
           "invoiceNumber='" + invoiceNumber + '\'' +
           ", version=" + version +
           ", status=" + status +
           ", customerId='" + customerId + '\'' +
           ", items=" + items +
           ", notes='" + notes + '\'' +
           ", paymentInstructions='" + paymentInstructions + '\'' +
           ", issueDate=" + issueDate +
           ", dueDate=" + dueDate +
           ", paidDate=" + paidDate +
           ", createdAt=" + createdAt +
           ", createdBy='" + createdBy + '\'' +
           ", updatedAt=" + updatedAt +
           ", updatedBy='" + updatedBy + '\'' +
           '}';
  }
}
