package com.interview.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public interface BaseEntityDTO {
    @Schema(description = "Unique identifier of the entity", example = "1")
    Long id();

    @Schema(description = "The user who created the entity", example = "system")
    String createdBy();

    @Schema(description = "The date and time when the entity was created", example = "2023-10-01T12:00:00Z")
    Instant createdDate();

    @Schema(description = "The user who last modified the entity", example = "system")
    String lastModifiedBy();

    @Schema(description = "The date and time when the entity was last modified", example = "2023-10-05T14:48:00Z")
    Instant lastModifiedDate();

}
