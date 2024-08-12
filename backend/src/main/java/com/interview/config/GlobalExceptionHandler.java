package com.interview.config;

import static com.interview.config.GameConstants.PATH_NOT_FOUND;

import com.interview.exception.GameRuntimeException;
import com.interview.model.ErrorDto;
import com.interview.model.ErrorResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception handlers so we can return custom response JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    // Convert the list of validation errors into readable ErrorDto objects
    List<ErrorDto> errors = new ArrayList<>();
    for (ObjectError error : ex.getBindingResult().getAllErrors()) {
      errors.add(
          ErrorDto.builder()
              .field(((FieldError) error).getField())
              .message(error.getDefaultMessage())
              .build());
    }
    ErrorResponseDto errorResponse =
        new ErrorResponseDto("Bad Request", 400, "Validation Exception", errors);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle the custom GameRuntimeException class; propagates the ErrorResponseDto object from the
   * Exception into the response object.
   *
   * @param ex GameRuntimeException
   * @return A responseEntity with the ErrorResponseDto from the Exception
   */
  @ExceptionHandler(GameRuntimeException.class)
  public ResponseEntity<ErrorResponseDto> handleGameExceptions(GameRuntimeException ex) {
    return new ResponseEntity<>(ex.getErrorResponseDto(), ex.getStatus());
  }

  /**
   * Fallback handler so that any exception will be wrapped in our custom ErrorResponseDto output.
   * Detects and handles some typical HTTP Exceptions and the rest default to Internal Server Error.
   *
   * @param ex Exception Any uncaught exception
   * @return A ResponseEntity with the customized ErrorResponseDto object
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> fallbackExceptionHandler(Exception ex) {
    // TODO: Handle more exception types - MessageNotReadable, MethodNotSupported, etc
    if (ex instanceof NoHandlerFoundException) {
      String path = ((NoHandlerFoundException) ex).getRequestURL();
      return new ResponseEntity<>(
          new ErrorResponseDto("Not Found", 404, String.format(PATH_NOT_FOUND, path), null),
          HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(
          new ErrorResponseDto("Internal Server Error", 500, ex.getMessage(), null),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
