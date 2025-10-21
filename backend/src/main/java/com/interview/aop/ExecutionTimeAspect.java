package com.interview.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        final var startTime = System.nanoTime();
        final var proceed = joinPoint.proceed();
        final var endTime = System.nanoTime();
        final var executionTimeMilis = (endTime - startTime) / 1000000;

        logger.trace("{} executed in {} ms", joinPoint.getSignature(), executionTimeMilis);

        return proceed;
    }
}
