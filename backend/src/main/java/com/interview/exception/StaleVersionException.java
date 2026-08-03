package com.interview.exception;

import java.util.UUID;

public class StaleVersionException extends RuntimeException {

  public StaleVersionException(UUID id, int expected, int actual) {
    super("Repair order %s has been modified: expected version %d but found %d"
        .formatted(id, expected, actual));
  }
}
