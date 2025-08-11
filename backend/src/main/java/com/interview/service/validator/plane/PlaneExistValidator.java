package com.interview.service.validator.plane;

import com.interview.jpa.repository.PlaneRepository;
import com.interview.service.validator.BusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class PlaneExistValidator implements BusinessValidator {

    private final PlaneRepository planeRepository;

    @Override
    public boolean supports(Class<?> paramClass) {
        return PlaneValidatorDefinition.Exist.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PlaneValidatorDefinition.Exist flightExist = (PlaneValidatorDefinition.Exist) target;

        if (!planeRepository.existsById(flightExist.planeId())) {
            errors.rejectValue("planeId", "plane.notFound", "Plane does not exist");
        }
    }
}
