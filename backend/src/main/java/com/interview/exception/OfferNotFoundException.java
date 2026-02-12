package com.interview.exception;

import java.util.UUID;

public class OfferNotFoundException extends ResourceNotFoundException {
  public OfferNotFoundException(final UUID offerId) {
    super("Attempted to find listing with id " + offerId + " but not found");
  }
}
