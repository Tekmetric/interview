package com.interview.dto.request;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import com.interview.persistence.enums.ApplicationStatus;

@Value
@Builder
@Schema(description = "Request to transition a credit application to a new status")
public class UpdateApplicationStatusRequest {

    @NotNull
    @Schema(
        description = "Target status. Valid transitions: SUBMITTED→UNDER_REVIEW, UNDER_REVIEW→APPROVED|DENIED",
        example = "UNDER_REVIEW"
    )
    ApplicationStatus status;

    public static class UpdateApplicationStatusRequestBuilder {
        public UpdateApplicationStatusRequest build() {
            Objects.requireNonNull(status, "status is required");
            return new UpdateApplicationStatusRequest(status);
        }
    }
}
