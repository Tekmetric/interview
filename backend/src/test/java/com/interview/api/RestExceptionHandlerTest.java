package com.interview.api;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import com.interview.api.request.VehicleRequest;
import com.interview.service.exception.CustomerNotFound;
import com.interview.service.exception.ServiceException;
import com.interview.service.exception.VehicleNotFound;
import com.interview.service.exception.WorkOrderNotFound;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    static Stream<ServiceException> notFoundExceptions() {
        final UUID id = UUID.randomUUID();
        return Stream.of(new CustomerNotFound(id), new VehicleNotFound(id), new WorkOrderNotFound(id));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notFoundExceptions")
    void notFoundExceptionReturns404(ServiceException exception) {
        final ResponseEntity<Map<String, Object>> response = handler.handleServiceException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.NOT_FOUND)
                .extractingByKey("error", as(InstanceOfAssertFactories.STRING))
                .isEqualTo(exception.getMessage());
    }

    @Test
    void validationExceptionReturnsBadRequest() {
        final BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(Mockito.mock(VehicleRequest.class), "vehicleRequest");
        bindingResult.rejectValue("vin", "NotNull", "VIN must not be blank");
        final MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(Mockito.mock(), bindingResult);

        final ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.BAD_REQUEST)
                .extractingByKey("errors", as(InstanceOfAssertFactories.LIST))
                .containsExactly("vin: VIN must not be blank");
        assertThat(response.getBody()).doesNotContainKey("error");
    }

    @Test
    void validationExceptionReturnsMultipleErrors() {
        final BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(Mockito.mock(VehicleRequest.class), "vehicleRequest");
        bindingResult.rejectValue("vin", "NotNull", "VIN must not be blank");
        bindingResult.rejectValue("vin", "Size", "VIN must be exactly 17 characters");
        final MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(Mockito.mock(), bindingResult);

        final ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .extractingByKey("errors", as(InstanceOfAssertFactories.LIST))
                .containsExactlyInAnyOrder("vin: VIN must not be blank", "vin: VIN must be exactly 17 characters");
    }

    @Test
    void messageNotReadableReturnsBadRequest() {
        final ResponseEntity<Map<String, Object>> response =
                handler.handleMessageNotReadable(new HttpMessageNotReadableException(
                        "JSON parse error", Mockito.mock(org.springframework.http.HttpInputMessage.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.BAD_REQUEST)
                .extractingByKey("error", as(InstanceOfAssertFactories.STRING))
                .contains("Malformed request body");
    }

    @Test
    void dataIntegrityViolationReturns409() {
        final ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("Unique index or primary key violation"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.CONFLICT)
                .extractingByKey("error", as(InstanceOfAssertFactories.STRING))
                .contains("conflicting record already exists");
    }

    @Test
    void unexpectedExceptionReturns500() {
        final ResponseEntity<Map<String, Object>> response = handler.handleUnexpected(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.INTERNAL_SERVER_ERROR)
                .containsEntry("error", "An unexpected error occurred");
    }
}
