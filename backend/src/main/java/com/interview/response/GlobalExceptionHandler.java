package com.interview.response;

import com.interview.exception.RepairJobNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RepairJobNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ApiError> handleRepairJobNotFound(
            RepairJobNotFoundException ex,
            HttpServletRequest request) {

        var error = new ApiError(
                now(),
                NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ApiError> handleJsonParseError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        var error = new ApiError(
                now(),
                BAD_REQUEST.value(),
                "Bad Request",
                "Invalid request body — check field formats and enum values",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ApiError> handleGeneralError(
            Exception ex,
            HttpServletRequest request) {

        var error = new ApiError(
                now(),
                INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
