package com.interview.exception;

import com.interview.model.ErrorDto;
import com.interview.model.ErrorResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;

@Getter
public class GameRuntimeException extends RuntimeException {
  private final HttpStatus status;
  private final ErrorResponseDto errorResponseDto;

  public GameRuntimeException(String message, HttpStatus status) {
    this(message, status, null);
  }

  public GameRuntimeException(String message, HttpStatus status, ErrorDto error) {
    this.errorResponseDto =
        new ErrorResponseDto(
            status.name(),
            status.value(),
            message,
            error != null ? Collections.singletonList(error) : null);
    this.status = status;
  }
}
