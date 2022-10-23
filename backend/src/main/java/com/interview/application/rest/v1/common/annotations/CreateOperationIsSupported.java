package com.interview.application.rest.v1.common.annotations;

import com.interview.application.rest.v1.common.AbstractCRUDResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to flag that the implementation of the {@link AbstractCRUDResource}
 * can handle the create operation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CreateOperationIsSupported {
}
