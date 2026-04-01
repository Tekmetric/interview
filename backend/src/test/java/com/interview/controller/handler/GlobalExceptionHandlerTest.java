package com.interview.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.interview.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleResourceNotFoundException_isLogged() {
    final ResourceNotFoundException exception =
        spy(new ResourceNotFoundException("Resource Not Found"));

    globalExceptionHandler.handleResourceNotFoundException(exception);

    verify(exception).getMessage();
  }

  @Test
  void handleMethodArgumentNotValid_returnsSummaryOfErrors() {
    final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    final BindingResult bindingResult = mock(BindingResult.class);
    final FieldError fieldError1 = new FieldError("foo", "name", "give me a name");
    final FieldError fieldError2 = new FieldError("foo", "thiny", "give me a thingy");

    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
    when(exception.getBindingResult()).thenReturn(bindingResult);

    final ResponseEntity<Map<String, String>> result =
        globalExceptionHandler.handleMethodArgumentNotValid(exception);

    assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    final Map<String, String> errors = result.getBody();
    assertEquals(errors.get(fieldError1.getField()), fieldError1.getDefaultMessage());
    assertEquals(errors.get(fieldError2.getField()), fieldError2.getDefaultMessage());
  }
}
