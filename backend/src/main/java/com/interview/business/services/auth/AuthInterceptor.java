package com.interview.business.services.auth;

import com.interview.business.services.auth.annotation.Authenticated;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthJwtService authJwtService;

    public AuthInterceptor(AuthJwtService authJwtService) {
        this.authJwtService = authJwtService;
    }

    private @Nullable String getAuthTokenString(HttpServletRequest request) {
        final var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        return authHeader.substring(7);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        final var authToken = getAuthTokenString(request);
        final var authentication = ((HandlerMethod) handler).getMethodAnnotation(Authenticated.class);


        if (authToken == null || authentication == null) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        authJwtService
                .parse(authToken)
                .ifPresent(token -> SecurityContextHolder.getContext().setAuthentication(token));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
