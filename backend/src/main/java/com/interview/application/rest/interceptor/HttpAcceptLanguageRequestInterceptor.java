package com.interview.application.rest.interceptor;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.application.rest.v1.common.PathConstants;
import com.interview.domain.service.common.RequestLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Locale;

/**
 * Custom spring interceptor to get the user language from the http request.
 */
@ExcludeFromLoggingAspect
@Component
@Slf4j
public class HttpAcceptLanguageRequestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final @NotNull HttpServletResponse response,
            final @NotNull Object handler) {
        if (request.getRequestURI() != null
                && request.getRequestURI().contains(PathConstants.PATH_PREFIX)) {
            String locale = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            if (locale != null) {
                log.info("Received HttpHeaders.ACCEPT_LANGUAGE is [{}]", locale);
            }
            RequestLocal.setLocale(locale == null ? Locale.ENGLISH : new Locale(locale));
        }
        return true;
    }
}
