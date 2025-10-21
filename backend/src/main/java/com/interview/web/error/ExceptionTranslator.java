package com.interview.web.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class ExceptionTranslator {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionTranslator.class);

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse onAccessDeniedException(final AccessDeniedException exception) {
        logger.error("onAccessDeniedException: ", exception);
        return new ErrorResponse("access-denied", "");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse onEntityNotFoundException(final EntityNotFoundException exception) {
        logger.error("onEntityNotFoundException: ", exception);
        return new ErrorResponse("entity-not-found", "");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onValidationException(final Exception exception) {
        logger.error("onValidationException: ", exception);

        return switch (exception) {
            case ConstraintViolationException e -> {
                final var message = e.getConstraintViolations()
                    .stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));
                yield new ErrorResponse("bad-request", message);
            }
            case HttpMessageNotReadableException e -> new ErrorResponse("bad-request", "invalid-request");
            default -> new ErrorResponse("bad-request", "invalid-request");
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        logger.error("onMethodArgumentNotValidException: ", exception);

        final var fieldErrors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .toList();

        final var objectErrors = exception.getBindingResult()
            .getGlobalErrors()
            .stream()
            .map(globalError -> globalError.getObjectName() + ": " + globalError.getDefaultMessage())
            .toList();

        final var allErrors = Stream
            .concat(fieldErrors.stream(), objectErrors.stream())
            .collect(Collectors.joining("; "));

        return new ErrorResponse("validation-error", allErrors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse onRuntimeException(final Exception exception) {
        logger.error("onRuntimeException: %s", exception);
        return new ErrorResponse("runtime-error", "Please contact API admin!");
    }
}
