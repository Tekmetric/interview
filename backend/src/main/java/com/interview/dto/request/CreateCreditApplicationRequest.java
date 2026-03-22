package com.interview.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.persistence.enums.LoanPurpose;
import com.interview.validation.ValidLoanAmount;

@Value
@Builder
@ValidLoanAmount
@Schema(description = "Request payload to submit a new credit application")
public class CreateCreditApplicationRequest {

    @NotNull
    @Schema(description = "ID of the customer submitting the application")
    UUID customerId;

    @NotNull
    @Positive
    @Schema(description = "Requested loan amount in USD", example = "35000.00")
    BigDecimal requestedLoanAmount;

    @NotNull
    @Schema(description = "Purpose of the loan", example = "VEHICLE_PURCHASE")
    LoanPurpose loanPurpose;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Total existing monthly debt obligations in USD", example = "500.00")
    BigDecimal monthlyDebt;

    @Size(max = 1000)
    @Schema(description = "Optional applicant notes", example = "First-time buyer, stable employment for 5 years")
    String notes;

    @NotEmpty
    @Valid
    @Schema(description = "Supporting documents required for the application (proof of income, ID, etc.)")
    List<SupportingDocumentRequest> documents;

    public static class CreateCreditApplicationRequestBuilder {
        public CreateCreditApplicationRequest build() {
            Objects.requireNonNull(customerId,          "customerId is required");
            Objects.requireNonNull(requestedLoanAmount, "requestedLoanAmount is required");
            Objects.requireNonNull(loanPurpose,         "loanPurpose is required");
            Objects.requireNonNull(monthlyDebt,         "monthlyDebt is required");
            Objects.requireNonNull(documents,           "documents is required");
            return new CreateCreditApplicationRequest(customerId, requestedLoanAmount, loanPurpose, monthlyDebt, notes, documents);
        }
    }
}
