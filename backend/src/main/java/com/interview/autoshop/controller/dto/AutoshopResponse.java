package com.interview.autoshop.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Schema(description = "An autoshop record as returned by the API.")
public class AutoshopResponse {

    @Schema(description = "Server-assigned identifier.", example = "1")
    Long id;

    @Schema(description = "Business name of the autoshop.", example = "Hopper Motors")
    String name;

    @Schema(description = "Street address.", example = "123 Main St, Austin, TX")
    String address;

    @Schema(description = "Contact phone number.", example = "555-0100")
    String phone;

    @Schema(description = "Timestamp when the record was created (UTC).", example = "2026-04-23T21:22:34.559256Z")
    Instant createdAt;

    @Schema(description = "Timestamp when the record was last updated (UTC).", example = "2026-04-23T21:22:34.559256Z")
    Instant updatedAt;
}
