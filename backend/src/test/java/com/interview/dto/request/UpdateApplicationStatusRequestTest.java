package com.interview.dto.request;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import com.interview.persistence.enums.ApplicationStatus;

class UpdateApplicationStatusRequestTest {

    @Test
    void build_missingStatus_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> UpdateApplicationStatusRequest.builder().build())
                .withMessageContaining("status");
    }

    @Test
    void build_allRequiredFieldsPresent_succeeds() {
        assertThatCode(() -> UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.UNDER_REVIEW)
                .build())
                .doesNotThrowAnyException();
    }
}
