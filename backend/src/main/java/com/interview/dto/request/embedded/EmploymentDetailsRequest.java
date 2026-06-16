package com.interview.dto.request.embedded;

import java.math.BigDecimal;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import com.interview.persistence.enums.EmploymentStatus;

@Value
@Builder
@Schema(description = "Customer employment and income profile")
public class EmploymentDetailsRequest {

    @NotNull
    @Schema(description = "Employment status", example = "EMPLOYED")
    EmploymentStatus employmentStatus;

    @Size(max = 200)
    @Schema(example = "Acme Corp")
    String employerName;

    @NotNull
    @Positive
    @Schema(description = "Annual gross income in USD", example = "95000.00")
    BigDecimal annualIncome;

    public static class EmploymentDetailsRequestBuilder {
        public EmploymentDetailsRequest build() {
            Objects.requireNonNull(employmentStatus, "employmentStatus is required");
            Objects.requireNonNull(annualIncome,     "annualIncome is required");
            return new EmploymentDetailsRequest(employmentStatus, employerName, annualIncome);
        }
    }
}
