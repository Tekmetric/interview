package com.interview.web.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    long capacity() default 1000;

    long timeValue() default 1;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}