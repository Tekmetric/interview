package com.interview.advice;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.interview.exception.AwsException;
import com.interview.exception.CreditApplicationNotFoundException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.DealershipException;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.InvalidApplicationStateException;
import com.interview.exception.S3DocumentDownloadException;
import com.interview.exception.S3DocumentUploadException;
import com.interview.exception.SqsPublishException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AwsException.class)
    public ProblemDetail handleAwsException(final AwsException ex) {
        log.error("AWS infrastructure operation failed: {}", ex.getMessage(), ex);
        return switch (ex) {
            case S3DocumentUploadException e ->
                    problem(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "document-upload-failed");
            case S3DocumentDownloadException e ->
                    problem(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "document-download-failed");
            case SqsPublishException e ->
                    problem(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "sqs-publish-failed");
        };
    }

    @ExceptionHandler(DealershipException.class)
    public ProblemDetail handleDealershipException(final DealershipException ex) {
        return switch (ex) {
            case CustomerNotFoundException e ->
                    problem(HttpStatus.NOT_FOUND, e.getMessage(), "customer-not-found");
            case CreditApplicationNotFoundException e ->
                    problem(HttpStatus.NOT_FOUND, e.getMessage(), "credit-application-not-found");
            case InvalidApplicationStateException e ->
                    problem(HttpStatus.CONFLICT, e.getMessage(), "invalid-application-state");
            case DuplicateResourceException e ->
                    problem(HttpStatus.CONFLICT, e.getMessage(), "duplicate-resource");
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(final MethodArgumentNotValidException ex) {
        final Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
        final List<String> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .map(message -> message != null ? message : "Invalid request")
                .toList();

        final ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Request validation failed", "validation-error");
        detail.setProperty("fieldErrors", fieldErrors);
        detail.setProperty("globalErrors", globalErrors);
        return detail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(final DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "A resource with the provided unique fields already exists", "duplicate-resource");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(final ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure on {}", ex.getPersistentClassName());
        return problem(HttpStatus.CONFLICT,
                "The resource was modified by another request. Please fetch the latest version and retry.",
                "optimistic-locking-failure");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(final Exception ex) {
        log.error("Unexpected error", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "internal-error");
    }

    private ProblemDetail problem(final HttpStatus status, final String detail, final String errorCode) {
        final ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setType(URI.create("https://api.tekmetric.com/errors/" + errorCode));
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }
}