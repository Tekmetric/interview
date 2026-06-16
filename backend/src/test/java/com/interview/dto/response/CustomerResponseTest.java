package com.interview.dto.response;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustomerResponseTest {

    @Test
    void build_missingId_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> CustomerResponse.builder().build())
                .withMessageContaining("id");
    }

    @Test
    void build_idPresent_succeeds() {
        assertThatCode(() -> CustomerResponse.builder()
                .id(UUID.randomUUID())
                .build())
                .doesNotThrowAnyException();
    }
}
