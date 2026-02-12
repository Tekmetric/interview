package com.interview.invoices.services;

public class InvoiceServiceException extends RuntimeException {

  private final ErrorCode errorCode;

  public InvoiceServiceException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public InvoiceServiceException(ErrorCode errorCode, String messageTemplate, Object... args) {
    super(String.format(messageTemplate, args));
    this.errorCode = errorCode;
  }

  public ErrorCode errorCode() {
    return errorCode;
  }

  public enum ErrorCode {
    INTERNAL_ERROR,
    ENTITY_ALREADY_EXISTS,
    ENTITY_NOT_FOUND,
    OPERATION_NOT_ALLOWED,
    CONFLICT
  }
}
