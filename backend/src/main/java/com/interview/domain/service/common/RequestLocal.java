package com.interview.domain.service.common;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

import java.util.Locale;

/**
 * Keeps a {@link ThreadLocal} with usefully information for the current http request.
 */
@ExcludeFromLoggingAspect
@Slf4j
public class RequestLocal {

    /** {@link ThreadLocal} instance. */
    private static final ThreadLocal<RequestLocalContext> contextHolder
            = new NamedThreadLocal<>("Request Local Context");

    /** Private constructor. */
    private RequestLocal() {
        throw new IllegalStateException("This class is not for instantiating!");
    }

    /**
     * Set current request {@link Locale} value.
     * @param locale the new {@link Locale}.
     */
    public static void setLocale(Locale locale) {
        RequestLocalContext requestLocalContext = getRequestLocalContext();
        if (locale != null) {
            log.debug("Setting locale on request local context to [{}]", locale);
            requestLocalContext.setLocale(locale);
        }
    }

    /**
     * Get the current request {@link Locale}.
     * @return the {@link Locale} value.
     */
    public static Locale getLocale() {
        RequestLocalContext requestLocalContext = contextHolder.get();
        Locale locale = Locale.ENGLISH;
        if (requestLocalContext != null) {
            locale = requestLocalContext.getLocale();
        }
        return locale;
    }

    private static RequestLocalContext getRequestLocalContext() {
        RequestLocalContext requestLocalContext = contextHolder.get();
        if (requestLocalContext == null) {
            requestLocalContext = new RequestLocalContext();
            contextHolder.set(requestLocalContext);
        }
        return requestLocalContext;
    }

    @Getter
    @Setter
    private static class RequestLocalContext {
        private Locale locale = Locale.ENGLISH;
    }
}
