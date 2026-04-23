package com.interview.error;

import com.interview.autoshop.AutoshopNotFoundException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
}
