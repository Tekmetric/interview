package com.bloggingservice.handler;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void shouldReturnAValidProblemDetail() {
        when(webRequest.getDescription(anyBoolean())).thenReturn("/api/v1/test");
        StaleObjectStateException ex = new StaleObjectStateException("test-entity", "1234");
        ProblemDetail problemDetail = globalExceptionHandler.handleStaleObjectException(ex, webRequest);

        assertThat(problemDetail.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        assertThat(problemDetail.getDetail(), startsWith("Conflict for resource"));
        assertThat(problemDetail.getInstance(), equalTo(URI.create("/api/v1/test")));
    }
}