package com.interview.exception;

import com.interview.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.interview.exception.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception exception) {
        log.error("Exception occurred while processing request", exception);
        return toErrorResponseDto(INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleException(NotFoundException exception) {
        log.error("NotFoundException occurred while processing request {}", exception.getDeveloperMessage());
        return toErrorResponseDto(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleException(DuplicateException exception) {
        log.error("DuplicateException occurred while processing request {}", exception.getDeveloperMessage());
        return toErrorResponseDto(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleException(InvalidDataException exception) {
        log.error("InvalidDataException occurred while processing request {}", exception.getDeveloperMessage());
        return toErrorResponseDto(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(InternalServiceException exception) {
        log.error("Exception occurred while processing request", exception);
        return toErrorResponseDto(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleException(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException occurred while processing request {}", exception.getMessage());

        return ErrorResponseDto.builder()
                .errorCode(ErrorCode.INVALID_DATA.getCode())
                .message("Invalid data provided for field : %s".formatted(exception.getFieldError().getField()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleException(final HttpMessageNotReadableException exception) {
        log.error("HttpMessageNotReadableException occurred while processing request {}", exception.getMessage());

        return ErrorResponseDto.builder()
                .errorCode(ErrorCode.INVALID_DATA.getCode())
                .message("Invalid data provided")
                .build();
    }


    private static ErrorResponseDto toErrorResponseDto(InternalServiceException exception) {
        return toErrorResponseDto(exception.getErrorCode());
    }

    private static ErrorResponseDto toErrorResponseDto(ErrorCode exception) {
        return ErrorResponseDto.builder()
                .errorCode(exception.getCode())
                .message(exception.getMessage()).build();
    }

}
