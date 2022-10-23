package com.interview.domain.service.common;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.domain.service.common.RequestLocal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Spring boot component that will manage internationalization.
 */
@Component
@ExcludeFromLoggingAspect
@RequiredArgsConstructor
public class InternationalizationService {
    private final MessageSource messageSource;

    /**
     * Get translation for specified key in the
     * {@link RequestLocal#getLocale()} locale.
     * @param key the key used to find the translation
     * @return the translation in the {@link RequestLocal#getLocale()} locale.
     */
    public String getTranslation(final String key) {
        return messageSource.getMessage(key, null, RequestLocal.getLocale());
    }

    /**
     * Get translation for specified key in the
     * {@link RequestLocal#getLocale()} locale.
     * @param key the key used to find the translation
     * @param args the runtime arguments used to compose the translation.
     * @return the translation in the {@link RequestLocal#getLocale()} locale.
     */
    public String getTranslation(final String key, final Object... args) {
        return messageSource.getMessage(key, args, RequestLocal.getLocale());
    }
}
