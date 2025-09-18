package com.interview.test.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

public record BearerTokenInterceptor(String token) implements ClientHttpRequestInterceptor {

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
