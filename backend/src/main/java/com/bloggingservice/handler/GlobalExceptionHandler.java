package com.bloggingservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(StaleObjectStateException.class)
  public ProblemDetail handleStaleObjectException(StaleObjectStateException ex) {
    log.info("Conflict updating resource: ", ex);
    return ProblemDetail.forStatus(HttpStatus.CONFLICT);
  }

  @ExceptionHandler(StaleStateException.class)
  public ProblemDetail handleStaleStateException(StaleStateException ex) {
    log.info("Conflict updating resource: ", ex);
    return ProblemDetail.forStatus(HttpStatus.CONFLICT);
  }
}
