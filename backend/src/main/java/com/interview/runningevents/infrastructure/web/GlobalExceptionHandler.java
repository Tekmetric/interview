package com.interview.runningevents.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.interview.runningevents.application.exception.RunningEventNotFoundException;
import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.infrastructure.web.dto.ErrorResponseDTO;

/**
 * Global exception handler for REST API controllers.
 * Converts exceptions to standardized error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles RunningEventNotFoundException.
     * Returns HTTP 404 Not Found.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 404 status
     */
    @ExceptionHandler(RunningEventNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRunningEventNotFoundException(
            RunningEventNotFoundException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ValidationException.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(ValidationException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException for @Valid annotation validation failures.
     * Returns HTTP 400 Bad Request with field-specific validation errors.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status and validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Create the error response with general info
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed. Check 'details' field for more information.")
                .path(extractPath(request))
                .build();

        // Add field errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(error.getField(), error.getDefaultMessage());
        }

        // Add global errors
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errorResponse.addValidationError(error.getObjectName(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles TypeMismatchException for invalid path variables or request parameters.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(TypeMismatchException ex, WebRequest request) {
        String errorMessage = ex.getValue() + " is not a valid value for " + ex.getPropertyName();

        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException matme = (MethodArgumentTypeMismatchException) ex;
            if (matme.getRequiredType() != null) {
                errorMessage += ". Expected type: " + matme.getRequiredType().getSimpleName();
            }
        }

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Type mismatch")
                .path(extractPath(request))
                .build()
                .addDetail(errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException for malformed JSON requests.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Malformed JSON request")
                .path(extractPath(request))
                .build();

        String rootCauseMessage = getRootCauseMessage(ex);
        if (rootCauseMessage != null) {
            errorResponse.addDetail(rootCauseMessage);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MissingServletRequestParameterException for missing required request parameters.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Missing required parameter")
                .path(extractPath(request))
                .build()
                .addValidationError(ex.getParameterName(), "Parameter is required");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles BindException for parameter binding errors.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDTO> handleBindException(BindException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Parameter binding failed")
                .path(extractPath(request))
                .build();

        // Add field errors
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->
                        errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles NoHandlerFoundException for invalid URLs.
     * Returns HTTP 404 Not Found.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 404 status
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all other uncaught exceptions.
     * Returns HTTP 500 Internal Server Error.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllUncaughtException(Exception ex, WebRequest request) {
        // Log the error
        logger.error("Unhandled exception", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please try again later.")
                .path(extractPath(request))
                .build();

        // In development environments, you might want to include the error details
        // errorResponse.addDetail("Exception: " + ex.getClass().getName() + " - " + ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts the path from the request.
     *
     * @param request The web request
     * @return The path
     */
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Gets the root cause message from an exception.
     *
     * @param ex The exception
     * @return The root cause message or null if not available
     */
    private String getRootCauseMessage(Exception ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }
}
