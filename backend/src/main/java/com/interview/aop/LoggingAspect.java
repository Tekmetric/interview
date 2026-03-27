package com.interview.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restAPIMethods() {}

    @Pointcut("execution(* com.interview.feature.project..*.*(..))")
    public void featureProjectMethods() {}

    @Around("restAPIMethods() || featureProjectMethods()")
    public Object logMethodArguments(final ProceedingJoinPoint joinPoint) throws Throwable {
        final var methodSignature = (MethodSignature) joinPoint.getSignature();
        final var argsName = methodSignature.getParameterNames() == null
            ? List.of()
            : List.of(methodSignature.getParameterNames());
        final var argsValue = joinPoint.getArgs() == null
            ? List.of()
            : Arrays.asList(joinPoint.getArgs());
        final var argPairs = IntStream
            .range(0, argsName.size())
            .mapToObj(index -> "%s: '%s'".formatted(argsName.get(index), argsValue.get(index)))
            .collect(Collectors.joining(", "));

        logger.trace("Method call: {}, args: [ {} ]", methodSignature, argPairs);
        final var response = joinPoint.proceed();
        if (shouldSkip(methodSignature)) {
            logger.trace("Skipping method call response: {}", methodSignature);
            return response;
        }
        logger.trace("Method call: {}, response: {}", methodSignature, response);

        return response;
    }

    private boolean shouldSkip(final MethodSignature methodSignature) {
        final var returnType = methodSignature.getReturnType();
        final var componentType = returnType.getComponentType();
        return returnType.equals(Byte[].class) || (componentType != null && componentType.equals(byte.class));
    }
}