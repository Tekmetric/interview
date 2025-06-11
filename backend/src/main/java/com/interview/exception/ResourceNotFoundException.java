package com.interview.exception;

/**
 * Exception thrown when a resource that is being searched for is not found. We can further extend
 * this and create specific exceptions for different resources but for the sake of simplicity, I
 * only created one.
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

}
