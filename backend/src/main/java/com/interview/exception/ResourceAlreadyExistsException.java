package com.interview.exception;

/**
 * Exception thrown when a resource that is being created already exists. This is typically used in
 * scenarios where a unique constraint is violated, such as trying to create a user with an email
 * that already exists. We can further extend this and create specific exceptions for different
 * resources but for the sake of simplicity, I only created one.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

  public ResourceAlreadyExistsException(String message) {
    super(message);
  }

}
