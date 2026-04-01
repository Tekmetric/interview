package com.interview.exception;

import java.util.UUID;

public class ListingNotFoundException extends ResourceNotFoundException {
  public ListingNotFoundException(final UUID listingId) {
    super("Attempted to find listing with id " + listingId + " but not found");
  }
}
