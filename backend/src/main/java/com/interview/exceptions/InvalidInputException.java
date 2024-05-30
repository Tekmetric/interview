package com.interview.exceptions;

public class InvalidInputException extends ServiceException{

  private String[] fields;
  public InvalidInputException(String... fields) {
    this.fields = fields;
  }

  public String[] getFields() {
    return fields;
  }
}
