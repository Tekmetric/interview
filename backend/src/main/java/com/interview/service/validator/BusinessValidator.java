package com.interview.service.validator;

import org.springframework.validation.Validator;

/**
 * Marker interface for business-rule validators.
 * Extends Spring's {@link Validator} to reuse the supports(..) and validate(..) methods.
 */
public interface BusinessValidator extends Validator {
}
