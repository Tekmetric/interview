package com.interview.application.aspect.log.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.interview.application.aspect.log.LoggingAspect;
import org.slf4j.LoggerFactory;

/**
 * Custom annotation used to exclude method parameters from {@link LoggingAspect}
 */
@Inherited
@Retention(RUNTIME)
@Target({ METHOD })
public @interface ExcludeParametersFromLogging {
}
