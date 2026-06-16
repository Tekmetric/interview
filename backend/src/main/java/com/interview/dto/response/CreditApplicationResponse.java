package com.interview.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import com.interview.aws.S3DocumentService;
import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.LoanPurpose;

@Value
@Builder(toBuilder = true)
@Schema(description = "Credit application resource representation")
public class CreditApplicationResponse {

    @Schema(description = "Unique application identifier (UUIDv7)")
    UUID id;

    @Schema(description = "ID of the customer who submitted this application")
    UUID customerId;

    @Schema(description = "Customer full name for display convenience")
    String customerName;

    @Schema(description = "Current application status", example = "UNDER_REVIEW")
    ApplicationStatus status;

    @Schema(description = "Requested loan amount in USD", example = "35000.00")
    BigDecimal requestedLoanAmount;

    @Schema(description = "Purpose of the loan", example = "VEHICLE_PURCHASE")
    LoanPurpose loanPurpose;

    @Schema(description = "Applicant's total monthly debt obligations in USD", example = "500.00")
    BigDecimal monthlyDebt;

    @Schema(description = "Optional underwriter or applicant notes")
    String notes;

    @Schema(description = "Timestamp when the application was submitted")
    ZonedDateTime submittedAt;

    @Schema(description = "Timestamp when a final decision (APPROVED/DENIED) was recorded")
    ZonedDateTime decidedAt;

    @Schema(description = "Timestamp when this record was created")
    ZonedDateTime dateCreated;

    @Schema(description = "Timestamp of the most recent update to this record")
    ZonedDateTime dateLastModified;

    @Schema(description = "Per-document presigned S3 PUT URLs (valid 15 min, create response only)")
    List<S3DocumentService.DocumentUpload> documentUploadUrls;

    @Schema(description = "Per-document presigned S3 GET URLs (valid 60 min, read responses)")
    List<S3DocumentService.DocumentDownload> documentDownloadUrls;

    public static class CreditApplicationResponseBuilder {
        public CreditApplicationResponse build() {
            Objects.requireNonNull(id,         "id is required");
            Objects.requireNonNull(customerId, "customerId is required");
            Objects.requireNonNull(status,     "status is required");
            return new CreditApplicationResponse(id, customerId, customerName, status,
                    requestedLoanAmount, loanPurpose, monthlyDebt, notes,
                    submittedAt, decidedAt, dateCreated, dateLastModified,
                    documentUploadUrls, documentDownloadUrls);
        }
    }
}
