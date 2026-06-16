package com.interview.dto.request.embedded;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class AddressRequestTest {

    @Test
    void build_missingStreet_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().street(null).build())
                .withMessageContaining("street");
    }

    @Test
    void build_missingCity_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().city(null).build())
                .withMessageContaining("city");
    }

    @Test
    void build_missingState_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().state(null).build())
                .withMessageContaining("state");
    }

    @Test
    void build_missingZipCode_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().zipCode(null).build())
                .withMessageContaining("zipCode");
    }

    @Test
    void build_allRequiredFieldsPresent_succeeds() {
        assertThatCode(() -> validBuilder().build()).doesNotThrowAnyException();
    }

    private AddressRequest.AddressRequestBuilder validBuilder() {
        return AddressRequest.builder()
                .street("100 Main St").city("Austin").state("TX").zipCode("78701");
    }
}
