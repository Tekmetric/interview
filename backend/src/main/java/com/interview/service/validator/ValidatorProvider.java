package com.interview.service.validator;

import com.interview.common.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Log4j2
@RequiredArgsConstructor
public class ValidatorProvider {

    private final ApplicationContext context;
    private final Validator beanValidator;
    private final Map<Class<?>, List<BusinessValidator>> businessValidatorsByType = new ConcurrentHashMap<>();

    /** Validates a DTO using bean validation and any matching business validators. */
    public <T> void validate(T dto) {
        performAnnotationValidation(dto);

        performBusinessValidation(dto);
    }

    private <T> void performBusinessValidation(T dto) {
        Class<?> dtoClass = dto.getClass();
        List<BusinessValidator> validators = businessValidatorsByType.computeIfAbsent(
                dtoClass, this::findBusinessValidatorsFor
        );

        Errors errors = new BeanPropertyBindingResult(dto, dtoClass.getSimpleName());
        validators.forEach(v -> v.validate(dto, errors));

        if (errors.hasErrors()) {
            String messages = errors.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : e.getCode())
                    .collect(Collectors.joining("; "));
            String codes = errors.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getCode)
                    .collect(Collectors.joining(","));
            throw new ValidationException(messages, codes);
        }
    }

    private <T> void performAnnotationValidation(T dto) {
        Set<ConstraintViolation<T>> violations = beanValidator.validate(dto);
        if (!violations.isEmpty()) {
            String messages = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            String codes = violations.stream()
                    .map(v -> v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                    .collect(Collectors.joining(","));
            throw new ValidationException(messages, codes);
        }
    }

    private List<BusinessValidator> findBusinessValidatorsFor(Class<?> dtoType) {
        Map<String, BusinessValidator> all = context.getBeansOfType(BusinessValidator.class);
        if (MapUtils.isEmpty(all)) {
            log.info("No business validators found for {}", dtoType);
            return Collections.emptyList();
        }

        List<BusinessValidator> matches = all.values().stream()
                .filter(v -> v.supports(dtoType))
                .collect(Collectors.toList());
        log.info("Cached {} BusinessValidator(s) for {}", matches.size(), dtoType.getSimpleName());

        return matches;
    }
}
