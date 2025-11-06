package com.interview._infrastructure.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.interview._infrastructure.domain.model.CustomError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> handleGenericException(Exception ex, HttpServletRequest request) {

        CustomError body = new CustomError(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomError> handleBadRequest(BadRequestException ex, HttpServletRequest request) {

        CustomError body = new CustomError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        CustomError body = new CustomError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomError> handleUnauthorized(UnauthorizedException ex,HttpServletRequest request) {
        CustomError body = new CustomError(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * This is to change how the Validation error messages are returned
     *
     * @param ex      - MethodArgumentNotValidException
     * @param headers - HttpHeaders
     * @param status  - HttpStatus
     * @param request - HttpRequest
     * @return - ResponseEntity with a Custom Error attached.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        String fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> String.format("%s: %s", fe.getField(), fe.getDefaultMessage()))
                .sorted()
                .collect(Collectors.joining(System.lineSeparator()));

        String path = null;

        if (request instanceof ServletWebRequest) {
            HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
            path = servletRequest.getRequestURI();
        }
        CustomError body = new CustomError(fieldErrors, HttpStatus.BAD_REQUEST, path);

        return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * To add a custom response for Jackson Serialization errors - e.g., bad ENUM values
     *
     * @param ex      - MethodArgumentNotValidException
     * @param headers - HttpHeaders
     * @param status  - HttpStatus
     * @param request - HttpRequest
     * @return - ResponseEntity with a Custom Error attached.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        Throwable cause = ex.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException ife =
                    (com.fasterxml.jackson.databind.exc.InvalidFormatException) cause;

            Class<?> targetType = ife.getTargetType();
            if (targetType.isEnum()) {
                CustomError body = getCustomError((ServletWebRequest) request, targetType, ife);

                return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
            }
        }
        // Fallback for other parse errors
        CustomError fallback = new CustomError(
                "Malformed JSON request",
                HttpStatus.BAD_REQUEST,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return handleExceptionInternal(ex, fallback, headers, HttpStatus.BAD_REQUEST, request);
    }

    private static CustomError getCustomError(ServletWebRequest request, Class<?> targetType, InvalidFormatException ife) {
        Object[] allowed = targetType.getEnumConstants();

        String fieldName = ife.getPath() != null && !ife.getPath().isEmpty()
                ? ife.getPath().get(0).getFieldName()
                : "unknown";

        String message = String.format(
                "Invalid value '%s' for field '%s'. Allowed values are: %s",
                ife.getValue(),
                fieldName,
                java.util.Arrays.toString(allowed)
        );

        return new CustomError(
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequest().getRequestURI()
        );
    }
}
