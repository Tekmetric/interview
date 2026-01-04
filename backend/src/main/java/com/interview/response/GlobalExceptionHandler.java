package com.interview.response;

import com.interview.exception.RepairJobNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RepairJobNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleRepairJobNotFound(
            RepairJobNotFoundException ex,
            HttpServletRequest request) {

        var error = new ApiError(
                LocalDateTime.now(),
                NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralError(
            Exception ex,
            HttpServletRequest request) {

        var error = new ApiError(
                LocalDateTime.now(),
                INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
