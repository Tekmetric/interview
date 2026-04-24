package com.interview.autoshop.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Schema(description = "Payload for creating a new autoshop.")
public class CreateAutoshopRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Business name of the autoshop.", example = "Swagger Motors", maxLength = 255)
    String name;

    @NotBlank
    @Size(max = 512)
    @Schema(description = "Street address.", example = "Localhost, 8080", maxLength = 512)
    String address;

    @NotBlank
    @Size(max = 32)
    @Schema(description = "Contact phone number.", example = "555-0100", maxLength = 32)
    String phone;
}
