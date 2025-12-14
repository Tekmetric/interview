package com.bloggingservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StaleObjectStateException.class)
    public ProblemDetail handleStaleObjectException(StaleObjectStateException ex, WebRequest request) {
        log.info("Conflict updating resource: ", ex);
        String detailedMessage = String.format("Conflict for resource %s", ex.getIdentifier());
        URI instance = URI.create(request.getDescription(false).replace("uri=", ""));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detailedMessage);
        problemDetail.setTitle(HttpStatus.CONFLICT.getReasonPhrase());
        problemDetail.setInstance(instance);

        return problemDetail;
    }
}
