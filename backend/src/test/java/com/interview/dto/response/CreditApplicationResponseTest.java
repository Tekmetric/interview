package com.interview.dto.response;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.interview.persistence.enums.ApplicationStatus;

class CreditApplicationResponseTest {

    @Test
    void build_missingId_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().id(null).build())
                .withMessageContaining("id");
    }

    @Test
    void build_missingCustomerId_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().customerId(null).build())
                .withMessageContaining("customerId");
    }

    @Test
    void build_missingStatus_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().status(null).build())
                .withMessageContaining("status");
    }

    @Test
    void build_allRequiredFieldsPresent_succeeds() {
        assertThatCode(() -> validBuilder().build()).doesNotThrowAnyException();
    }

    private CreditApplicationResponse.CreditApplicationResponseBuilder validBuilder() {
        return CreditApplicationResponse.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .status(ApplicationStatus.SUBMITTED);
    }
}
