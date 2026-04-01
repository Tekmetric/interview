package com.interview.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * This is an example of using an Aspect for cross-cutting logging concerns.
 *
 * We don't want to fill every rest controller method with boilerplate logging code, so we define it here as an aspect instead.
 * This aspect will log entry, exit, execution time, and exceptions for all methods in the controller and service packages.
 *
 * This is a rudimentary example. In production, we might enhance it with more features like user context to track who is
 * accessing our services and what data is being processed, with care taken to not log sensitive info.
 *
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.interview.controller..*(..)) || execution(* com.interview.service..*(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String method = ms.getDeclaringType().getSimpleName() + "." + ms.getName();
        log.debug("Enter {}(args:{})", method, pjp.getArgs().length);
        try {
            Object result = pjp.proceed();
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            log.debug("Exit {} -> {}ms", method, tookMs);
            return result;
        } catch (Throwable t) {
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            log.error("Exception in {} after {}ms: {}", method, tookMs, t.getMessage());
            throw t;
        }
    }
}
