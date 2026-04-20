package com.interview.exception;

import java.util.UUID;

public class RepairOrderNotFoundException extends RuntimeException {

  public RepairOrderNotFoundException(UUID id) {
    super("Repair order not found: " + id);
  }
}
