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
                .message("Bad Request")
                .path("/api/running-events")
                .build();

        // Then
        assertThat(dto.getStatus()).isEqualTo(400);
        assertThat(dto.getMessage()).isEqualTo("Bad Request");
        assertThat(dto.getPath()).isEqualTo("/api/running-events");
        assertThat(dto.getTimestamp()).isGreaterThan(0);
        assertThat(dto.getErrors()).isEmpty();
    }

    @Test
    public void shouldAddValidationErrors() {
        // Given
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .message("Validation Error")
                .path("/api/running-events")
                .build();

        // When
        dto.addValidationError("name", "Name is required");
        dto.addValidationError("dateTime", "Date and time is required");

        // Then
        assertThat(dto.getErrors()).hasSize(2);
        assertThat(dto.getErrors().get(0).getField()).isEqualTo("name");
        assertThat(dto.getErrors().get(0).getMessage()).isEqualTo("Name is required");
        assertThat(dto.getErrors().get(1).getField()).isEqualTo("dateTime");
        assertThat(dto.getErrors().get(1).getMessage()).isEqualTo("Date and time is required");
    }

    @Test
    public void shouldSupportMethodChaining() {
        // Given/When
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .message("Validation Error")
                .path("/api/running-events")
                .build()
                .addValidationError("name", "Name is required")
                .addValidationError("location", "Location is required");

        // Then
        assertThat(dto.getErrors()).hasSize(2);
        assertThat(dto.getErrors().get(0).getField()).isEqualTo("name");
        assertThat(dto.getErrors().get(1).getField()).isEqualTo("location");
    }

    @Test
    public void shouldInitializeErrorsIfNull() {
        // Given
        ErrorResponseDTO dto = new ErrorResponseDTO();
        dto.setErrors(null);

        // When
        dto.addValidationError("name", "Name is required");

        // Then
        assertThat(dto.getErrors()).isNotNull();
        assertThat(dto.getErrors()).hasSize(1);
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
}
