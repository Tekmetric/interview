package com.interview.resource.model.response;

public class ErrorResponse {
  private String message;

  public ErrorResponse(String message) {
    this.message = message;
  }

  // Getters and Setters
  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
