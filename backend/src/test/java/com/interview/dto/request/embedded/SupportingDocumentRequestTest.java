package com.interview.dto.request.embedded;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.interview.persistence.enums.SupportingDocumentType;

class SupportingDocumentRequestTest {

    @Test
    void build_missingDocumentType_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> SupportingDocumentRequest.builder()
                        .documentType(null)
                        .build())
                .withMessageContaining("documentType");
    }

    @Test
    void build_withDocumentTypeOnly_succeeds() {
        assertThatCode(() -> SupportingDocumentRequest.builder()
                .documentType(SupportingDocumentType.PROOF_OF_INCOME)
                .build())
                .doesNotThrowAnyException();
    }

    @Test
    void build_withOptionalFileName_succeeds() {
        SupportingDocumentRequest request = SupportingDocumentRequest.builder()
                .documentType(SupportingDocumentType.GOVERNMENT_ID)
                .fileName("drivers_license.jpg")
                .build();

        assertThat(request.getDocumentType()).isEqualTo(SupportingDocumentType.GOVERNMENT_ID);
        assertThat(request.getFileName()).isEqualTo("drivers_license.jpg");
    }
}
