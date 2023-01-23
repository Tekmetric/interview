package com.interview.config;

import com.interview.dto.ApiResponseDto;
import com.interview.exception.BadRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.exception.UnauthorizedRequestException;
import com.interview.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingConfiguration.class);

    protected MessageService messageService;

    public ExceptionHandlingConfiguration(MessageService messageService) {
        this.messageService = messageService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponseDto handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return new ApiResponseDto(false, ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map((error) -> messageService.getMessage(error.getDefaultMessage()))
                .collect(Collectors.joining(",")));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiResponseDto handleBadRequestException(BadRequestException ex) {
        logger.error(ex.getMessage());
        return new ApiResponseDto(false, messageService.getMessage(ex.getLocalizedMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponseDto handleBadRequestException(ResourceNotFoundException ex) {
        logger.error(ex.getMessage());
        return new ApiResponseDto(false, messageService.getMessage(ex.getLocalizedMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {UnauthorizedRequestException.class, AuthenticationException.class, AccessDeniedException.class})
    public ApiResponseDto handleUnauthorized() {
        return new ApiResponseDto(false, messageService.getMessage("invalidCredentials"));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ApiResponseDto handleAnyException(Exception e) {
        logger.error("Error while processing exception", e);
        return new ApiResponseDto(false, messageService.getMessage("somethingWrong"));
    }
}