package com.bloggingservice.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @InjectMocks private GlobalExceptionHandler globalExceptionHandler;

  @Test
  void shouldReturnAValidProblemDetailForStaleObjects() {
    StaleObjectStateException ex = new StaleObjectStateException("test-entity", "1234");
    ProblemDetail problemDetail = globalExceptionHandler.handleStaleObjectException(ex);

    assertThat(problemDetail.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
  }

  @Test
  void shouldReturnAValidProblemDetailForStaleStates() {
    StaleStateException ex = new StaleStateException("Result not expected");
    ProblemDetail problemDetail = globalExceptionHandler.handleStaleStateException(ex);

    assertThat(problemDetail.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
  }
}
