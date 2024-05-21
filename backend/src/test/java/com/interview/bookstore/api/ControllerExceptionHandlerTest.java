package com.interview.bookstore.api;

import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.exception.DuplicateFieldException;
import com.interview.bookstore.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Locale;

import static com.interview.bookstore.api.ErrorMessageCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerExceptionHandlerTest {

    private static final String GENERIC_ERROR_MESSAGE = "Generic error message";
    private static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Empty or invalid request body.";
    private static final String RESOURCE_NOT_FOUND_ERROR_MESSAGE = "Resource not found";
    private static final String DUPLICATE_FIELD_ERROR_MESSAGE = "Field value already exists.";
    private static final String FIELD_VALIDATION_ERROR_MESSAGE = "Field validations failed.";
    private static final String NULL_FIELD = "TITLE";
    private static final String NEGATIVE_FIELD = "PRICE";
    private static final String NULL_FIELD_VALIDATION = "Value must be provided.";
    private static final String NEGATIVE_FIELD_VALIDATION = "Value must be positive.";

    private ControllerExceptionHandler exceptionHandler;
    @Mock private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ControllerExceptionHandler(messageSource);
    }

    @Test
    void handleValidationExceptions() {
        when(messageSource.getMessage(eq(VALIDATION_ERROR_MESSAGE.code()), any(), eq(Locale.US)))
                .thenReturn(FIELD_VALIDATION_ERROR_MESSAGE);
        var invalidMethodArgException = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(BindingResult.class);
        when(invalidMethodArgException.getBindingResult()).thenReturn(bindingResult);

        var nullFieldError = new FieldError("updateBookDTO", NULL_FIELD, NULL_FIELD_VALIDATION);
        var negativeFieldError = new FieldError("updateBookDTO", NEGATIVE_FIELD, NEGATIVE_FIELD_VALIDATION);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(nullFieldError, negativeFieldError));

        ResponseEntity<ApiErrorResponse> responseEntity =
                exceptionHandler.handleValidationErrors(invalidMethodArgException, Locale.US);

        assertResponseEntity(responseEntity, HttpStatus.BAD_REQUEST,
                ErrorReasonCode.FIELD_VALIDATION, FIELD_VALIDATION_ERROR_MESSAGE);
        assertThat(responseEntity.getBody().getProperties())
                .contains(entry("validations", List.of(
                        new ApiValidationError(NULL_FIELD, NULL_FIELD_VALIDATION),
                        new ApiValidationError(NEGATIVE_FIELD, NEGATIVE_FIELD_VALIDATION)
                )));
    }


    @Test
    void handleDuplicateFieldException() {
        Class<Book> resourceType = Book.class;
        String fieldName = "id";

        when(messageSource.getMessage(eq(DUPLICATE_FIELD_ERROR.code()), any(), eq(Locale.US)))
                .thenReturn(DUPLICATE_FIELD_ERROR_MESSAGE);
        var duplicateFieldException = new DuplicateFieldException(resourceType, fieldName);

        ResponseEntity<ApiErrorResponse> responseEntity =
                exceptionHandler.duplicateFieldException(duplicateFieldException, Locale.US);

        assertResponseEntity(responseEntity, HttpStatus.BAD_REQUEST,
                ErrorReasonCode.DUPLICATE_FIELD_VALUE, DUPLICATE_FIELD_ERROR_MESSAGE);
        assertThat(responseEntity.getBody().getProperties())
                .contains(entry("resource", resourceType.getSimpleName()))
                .contains(entry("field", fieldName));
    }

    @Test
    void handleResourceNotFoundException() {
        when(messageSource.getMessage(eq(RESOURCE_NOT_FOUND_ERROR.code()), any(), eq(Locale.US)))
                .thenReturn(RESOURCE_NOT_FOUND_ERROR_MESSAGE);
        var notFoundException = new ResourceNotFoundException(Book.class, 1L);

        ResponseEntity<ApiErrorResponse> responseEntity =
                exceptionHandler.handleResourceNotFoundException(notFoundException, Locale.US);

        assertNoPropResponseEntity(responseEntity, HttpStatus.NOT_FOUND,
                ErrorReasonCode.RESOURCE_NOT_FOUND, RESOURCE_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    void handleInvalidRequestBodyException() {
        when(messageSource.getMessage(eq(INVALID_REQUEST_BODY_SYNTAX.code()), any(), eq(Locale.US)))
                .thenReturn(INVALID_REQUEST_BODY_ERROR_MESSAGE);

        ResponseEntity<ApiErrorResponse> responseEntity = exceptionHandler.invalidRequestBody(Locale.US);

        assertNoPropResponseEntity(responseEntity, HttpStatus.BAD_REQUEST,
                ErrorReasonCode.INVALID_REQUEST_BODY, INVALID_REQUEST_BODY_ERROR_MESSAGE);
    }

    @Test
    void handleUncaughtExceptions() {
        when(messageSource.getMessage(eq(UNEXPECTED_ERROR.code()), any(), eq(Locale.US)))
                .thenReturn(GENERIC_ERROR_MESSAGE);

        ResponseEntity<ApiErrorResponse> responseEntity =
                exceptionHandler.uncaughtExceptionHandler(new RuntimeException(), Locale.US);

        assertNoPropResponseEntity(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorReasonCode.GENERIC_ERROR, GENERIC_ERROR_MESSAGE);
    }

    private void assertNoPropResponseEntity(ResponseEntity<ApiErrorResponse> responseEntity, HttpStatus httpStatus,
                                            ErrorReasonCode reasonCode, String errorMessage) {
        ApiErrorResponse errorResponse = assertResponseEntity(responseEntity, httpStatus, reasonCode, errorMessage);
        assertThat(errorResponse.getProperties()).isEmpty();
    }

    private ApiErrorResponse assertResponseEntity(ResponseEntity<ApiErrorResponse> responseEntity, HttpStatus httpStatus,
                                                  ErrorReasonCode reasonCode, String errorMessage) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(httpStatus);

        ApiErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse.getReasonCode()).isEqualTo(reasonCode);
        assertThat(errorResponse.getReasonMessage()).isEqualTo(errorMessage);
        return errorResponse;
    }
}
