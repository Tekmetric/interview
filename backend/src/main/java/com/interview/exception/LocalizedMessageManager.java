package com.interview.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizedMessageManager {

    private final MessageSource messageSource;

    public ErrorResource buildErrorResource (BaseException exception, Locale locale) {
        String localizedMessage = messageSource.getMessage(exception.getCode(), null, exception.getMessage(), locale);
        return ErrorResource.builder()
                .errorCode(exception.getCode())
                .message(localizedMessage)
                .build();
    }
}
