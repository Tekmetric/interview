package com.interview.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@ResponseBody
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LocalizedMessageManager localizedMessageManager;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResource handleException(Exception exception){
        log.error("handleException", exception);
        MDC.clear();
        return ErrorResource.builder().errorCode("unknown-error").message("error").build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResource handleNotFoundException(BaseException exception){
        log.error("handleNotFoundException", exception);
        MDC.clear();
        return localizedMessageManager.buildErrorResource(exception, Locale.of(String.valueOf(Locale.ENGLISH)));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResource handleArgumentNotValidException(Exception exception){
        log.error("handleArgumentNotValidException", exception);
        MDC.clear();
        return ErrorResource.builder().errorCode("bad-request").message("something is wrong with your request").build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateResourceException.class)
    public ErrorResource handleDuplicateResourceException(BaseException exception){
        log.error("handleDuplicateResourceException", exception);
        MDC.clear();
        return localizedMessageManager.buildErrorResource(exception, Locale.of(String.valueOf(Locale.ENGLISH)));    }

}
