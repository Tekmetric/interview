package com.interview.dto.request.embedded;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import com.interview.persistence.enums.SupportingDocumentType;

@Value
@Builder
@Schema(description = "A supporting document to accompany the credit application")
public class SupportingDocumentRequest {

    @NotNull
    @Schema(description = "Type of supporting document", example = "PROOF_OF_INCOME")
    SupportingDocumentType documentType;

    @Schema(description = "Original file name — used as a content-type hint for the presigned PUT",
            example = "paystub_jan_2024.pdf")
    String fileName;

    public static class SupportingDocumentRequestBuilder {
        public SupportingDocumentRequest build() {
            Objects.requireNonNull(documentType, "documentType is required");
            return new SupportingDocumentRequest(documentType, fileName);
        }
    }
}
