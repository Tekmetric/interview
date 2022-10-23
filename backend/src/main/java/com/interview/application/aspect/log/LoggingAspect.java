package com.interview.application.aspect.log;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.application.aspect.log.annotations.ExcludeParametersFromLogging;
import com.interview.application.aspect.log.annotations.ExcludeResultFromLogging;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Logging aspect used to log helpful information(request, response, execution duration)
 * for all rest endpoints and business services.
 * <p/>
 * To exclude from the logging process {@link ExcludeFromLoggingAspect} annotation can be used on type or method.
 * <p/>
 * To exclude parameters from the logging process {@link ExcludeParametersFromLogging} annotation can be used on method.
 * <p/>
 * To exclude results from the logging process {@link ExcludeResultFromLogging} annotation can be used on method.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    private static final String HIDE_VALUE_FROM_LOGS = "*****";

    @Pointcut("within(com.interview.domain.service..*) || " +
            "target(com.interview.application.rest.v1.common.AbstractResource) ")
    public void loggingPointcut() {
        // method created to define the pointcut
    }

    @Around("loggingPointcut()")
    @SuppressWarnings("unchecked")
    public Object logAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        boolean excludeParameters = false;
        boolean excludeResult = false;
        boolean logMethodInfo = false;
        Method method = ((MethodSignature) signature).getMethod();
        if (method != null) {
            logMethodInfo = !method.isAnnotationPresent(ExcludeFromLoggingAspect.class)
                    && !signature.getDeclaringType().isAnnotationPresent(ExcludeFromLoggingAspect.class)
                    && !joinPoint.getTarget().getClass().isAnnotationPresent(ExcludeFromLoggingAspect.class);
            excludeParameters = method.isAnnotationPresent(ExcludeParametersFromLogging.class);
            excludeResult = method.isAnnotationPresent(ExcludeResultFromLogging.class);
        }

        if (logMethodInfo) {
            log.info("Enter: {}.{}() for target class {} with argument[s] = {}",
                    signature.getDeclaringType().getSimpleName(),
                    signature.getName(),
                    joinPoint.getTarget().getClass().getSimpleName(),
                    excludeParameters ? HIDE_VALUE_FROM_LOGS : Arrays.toString(joinPoint.getArgs()));
        }

        // proceed with the call
        Object result = joinPoint.proceed();

        long durationTime = System.currentTimeMillis() - startTime;
        if (logMethodInfo) {
            String logResult = "";
            if (!method.getReturnType().equals(Void.TYPE) && result != null) {
                logResult = excludeResult ? HIDE_VALUE_FROM_LOGS : result.toString();
            }
            log.info("Exit: {}.{}() took [{}] ms for target class {} {}",
                    signature.getDeclaringType().getSimpleName(),
                    signature.getName(),
                    durationTime,
                    joinPoint.getTarget().getClass().getSimpleName(),
                    logResult);
        }
        return result;
    }
}
