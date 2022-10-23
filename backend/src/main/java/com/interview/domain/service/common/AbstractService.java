package com.interview.domain.service.common;


import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.domain.exception.ApplicationException;

import java.util.function.Supplier;

/**
 * Base service.
 */
public abstract class AbstractService {

    @ExcludeFromLoggingAspect
    protected ApplicationException processException(Exception ex, Supplier<ApplicationException> applicationExceptionSupplier) {
        return (ex instanceof ApplicationException) ? (ApplicationException) ex : applicationExceptionSupplier.get();
    }
}
