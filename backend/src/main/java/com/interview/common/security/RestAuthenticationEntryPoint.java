package com.interview.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.common.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = resolveMessage(authException);

        response.setStatus(status.value());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"work-order-api\"");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                Map.of()
        );
        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private String resolveMessage(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return "Invalid username or password";
        }
        if (ex instanceof DisabledException) {
            return "User account is disabled";
        }
        if (ex instanceof InsufficientAuthenticationException) {
            return "Authentication is required to access this resource";
        }
        return "Authentication failed";
    }
}
