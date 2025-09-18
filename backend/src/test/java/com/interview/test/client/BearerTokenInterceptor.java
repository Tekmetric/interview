package com.interview.test.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

public class BearerTokenInterceptor implements ClientHttpRequestInterceptor {

    private final String token;

    public BearerTokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            HttpRequest request,
            @NonNull byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setBearerAuth(this.token);
        return execution.execute(request, body);
    }
}
