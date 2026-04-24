package com.interview.error;

import com.interview.error.exception.AutoshopNotFoundException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AutoshopNotFoundException.class)
    public ProblemDetail notFound(AutoshopNotFoundException ex) {
        log.warn("Autoshop not found: id={}", ex.getId());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("/problems/not-found"));
        pd.setTitle("Autoshop not found");
        return pd;
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail invalidProperty(PropertyReferenceException ex) {
        log.warn("Invalid query parameter: property={}", ex.getPropertyName());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Unknown property '" + ex.getPropertyName() + "' — check sort/filter parameters.");
        pd.setType(URI.create("/problems/invalid-query-parameter"));
        pd.setTitle("Invalid query parameter");
        pd.setProperty("property", ex.getPropertyName());
        return pd;
    }

    /** Overrides the parent's template method so validation errors keep our ProblemDetail shape (including the errors map). */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a, b) -> a));
        log.warn("Validation failed: errors={}", errors);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setType(URI.create("/problems/validation"));
        pd.setTitle("Invalid request");
        pd.setProperty("errors", errors);
        return handleExceptionInternal(ex, pd, headers, status, request);
    }

    /** Catch-all for anything not matched by a more specific handler or by {@link ResponseEntityExceptionHandler}. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.");
        pd.setType(URI.create("/problems/internal-error"));
        pd.setTitle("Internal server error");
        return pd;
    }
}
