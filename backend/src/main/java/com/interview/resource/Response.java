package com.interview.resource;

public class Response {
  private String error;
  private int responseCode;

  public Response(String error, int responseCode) {
    this.error = error;
    this.responseCode = responseCode;
  }

  public String getError() {
    return this.error;
  }

  public int getResponseCode() {
    return this.responseCode;
  }
}
