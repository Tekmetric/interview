package com.interview.error;

import com.interview.error.exception.AutoshopNotFoundException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AutoshopNotFoundException.class)
    public ProblemDetail notFound(AutoshopNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("/problems/not-found"));
        pd.setTitle("Autoshop not found");
        return pd;
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail invalidProperty(PropertyReferenceException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Unknown property '" + ex.getPropertyName() + "' — check sort/filter parameters.");
        pd.setType(URI.create("/problems/invalid-query-parameter"));
        pd.setTitle("Invalid query parameter");
        pd.setProperty("property", ex.getPropertyName());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a, b) -> a));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setType(URI.create("/problems/validation"));
        pd.setTitle("Invalid request");
        pd.setProperty("errors", errors);
        return pd;
    }
}
