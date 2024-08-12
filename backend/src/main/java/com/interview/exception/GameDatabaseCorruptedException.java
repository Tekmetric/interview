package com.interview.exception;

import org.springframework.http.*;

public class GameDatabaseCorruptedException extends GameRuntimeException {

  public GameDatabaseCorruptedException(String message, HttpStatus status) {
    super(message, status, null);
  }
}
