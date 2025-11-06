package com.interview._infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview._infrastructure.domain.model.CustomError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_PATH = "/api/v0";
    private static final String X_AUTH_KEY_HEADER = "X-AUTH-KEY";

    private final String expectedApiKey;
    private final ObjectMapper objectMapper;

    public ApiKeyAuthFilter(@Value("${demo.api-key}") String expectedApiKey,
                            ObjectMapper objectMapper) {
        this.expectedApiKey = expectedApiKey;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(X_AUTH_KEY_HEADER);

        if (request.getRequestURI().equals(AUTHENTICATED_PATH) || request.getRequestURI().startsWith(AUTHENTICATED_PATH + "/")) {

            if (apiKey == null || !apiKey.equals(expectedApiKey)) {
                CustomError error = new CustomError("You do not have access to this resource!",
                        HttpStatus.UNAUTHORIZED,
                        request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), error);

                return;

            }
            // set Authentication for Authenticated paths
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "api-key-user",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
