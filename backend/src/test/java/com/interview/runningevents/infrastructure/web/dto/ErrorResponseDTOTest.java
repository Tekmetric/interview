package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.interview.runningevents.infrastructure.web.dto.ErrorResponseDTO.ValidationErrorDTO;

public class ErrorResponseDTOTest {

    @Test
    public void shouldCreateValidDTO() {
        // Given
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
                .path("/api/running-events")
                .build();

        // Then
        assertThat(dto.getStatus()).isEqualTo(400);
        assertThat(dto.getError()).isEqualTo("Bad Request");
        assertThat(dto.getMessage()).isEqualTo("Validation failed");
        assertThat(dto.getPath()).isEqualTo("/api/running-events");
        assertThat(dto.getTimestamp()).isGreaterThan(0);
        assertThat(dto.getDetails()).isEmpty();
    }

    @Test
    public void shouldAddValidationErrors() {
        // Given
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation Error")
                .path("/api/running-events")
                .build();

        // When
        dto.addValidationError("name", "Name is required");
        dto.addValidationError("dateTime", "Date and time is required");

        // Then
        assertThat(dto.getDetails()).hasSize(2);
        assertThat(dto.getDetails().get(0).getField()).isEqualTo("name");
        assertThat(dto.getDetails().get(0).getMessage()).isEqualTo("Name is required");
        assertThat(dto.getDetails().get(1).getField()).isEqualTo("dateTime");
        assertThat(dto.getDetails().get(1).getMessage()).isEqualTo("Date and time is required");
    }

    @Test
    public void shouldAddGeneralErrorDetails() {
        // Given
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation Error")
                .path("/api/running-events")
                .build();

        // When
        dto.addDetail("This is a general error message");
        dto.addDetail("This is another general error message");

        // Then
        assertThat(dto.getDetails()).hasSize(2);
        assertThat(dto.getDetails().get(0).getField()).isNull();
        assertThat(dto.getDetails().get(0).getMessage()).isEqualTo("This is a general error message");
        assertThat(dto.getDetails().get(1).getField()).isNull();
        assertThat(dto.getDetails().get(1).getMessage()).isEqualTo("This is another general error message");
    }

    @Test
    public void shouldSupportMethodChaining() {
        // Given/When
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation Error")
                .path("/api/running-events")
                .build()
                .addValidationError("name", "Name is required")
                .addDetail("General error message");

        // Then
        assertThat(dto.getDetails()).hasSize(2);
        assertThat(dto.getDetails().get(0).getField()).isEqualTo("name");
        assertThat(dto.getDetails().get(1).getField()).isNull();
    }

    @Test
    public void shouldInitializeDetailsIfNull() {
        // Given
        ErrorResponseDTO dto = new ErrorResponseDTO();
        dto.setDetails(null);

        // When
        dto.addValidationError("name", "Name is required");
        dto.addDetail("General error message");

        // Then
        assertThat(dto.getDetails()).isNotNull();
        assertThat(dto.getDetails()).hasSize(2);
    }

    @Test
    public void validationErrorDTOShouldWork() {
        // Given
        ValidationErrorDTO errorDto = new ValidationErrorDTO("name", "Name is required");

        // Then
        assertThat(errorDto.getField()).isEqualTo("name");
        assertThat(errorDto.getMessage()).isEqualTo("Name is required");

        // When
        errorDto.setField("location");
        errorDto.setMessage("Location is required");

        // Then
        assertThat(errorDto.getField()).isEqualTo("location");
        assertThat(errorDto.getMessage()).isEqualTo("Location is required");
    }

    @Test
    public void shouldSetAndGetProperties() {
        // Given
        ErrorResponseDTO dto = new ErrorResponseDTO();

        // When
        dto.setStatus(404);
        dto.setError("Not Found");
        dto.setMessage("Resource not found");
        dto.setTimestamp(1234567890L);
        dto.setPath("/api/events/999");

        // Then
        assertThat(dto.getStatus()).isEqualTo(404);
        assertThat(dto.getError()).isEqualTo("Not Found");
        assertThat(dto.getMessage()).isEqualTo("Resource not found");
        assertThat(dto.getTimestamp()).isEqualTo(1234567890L);
        assertThat(dto.getPath()).isEqualTo("/api/events/999");
    }
}
