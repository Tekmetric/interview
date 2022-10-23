package com.interview.application.aspect.log.annotations;

import com.interview.application.aspect.log.LoggingAspect;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation used to exclude class or method from {@link LoggingAspect}
 */
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ExcludeFromLoggingAspect {
}
