package com.interview;

import lombok.NoArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor
public final class TestUtil {

    public static <T> void equalsVerifier(Class<T> clazz) throws Exception {
        T domainObject1 = clazz.getConstructor().newInstance();
        assertThat(domainObject1.toString()).isNotNull();
        assertThat(domainObject1).isEqualTo(domainObject1);
        assertThat(domainObject1).hasSameHashCodeAs(domainObject1);
        Object testOtherObject = new Object();
        assertThat(domainObject1).isNotEqualTo(testOtherObject);
        assertThat(domainObject1).isNotEqualTo(null);
        T domainObject2 = clazz.getConstructor().newInstance();
        assertThat(domainObject1).hasSameHashCodeAs(domainObject2);
    }
}
