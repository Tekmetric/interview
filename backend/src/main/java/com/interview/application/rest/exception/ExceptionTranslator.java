package com.interview.application.rest.exception;

import com.interview.domain.exception.*;
import com.interview.domain.service.common.InternationalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single point to handle HTTP exceptions.
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionTranslator {

    private final InternationalizationService internationalizationService;

    /**
     * Handler for {@link ApplicationException}
     * @param ex the exception
     * @return a {@link ResponseEntity}
     */
    @ExceptionHandler({ApplicationException.class})
    @ResponseBody
    public ResponseEntity<?> handleConstraintViolation(final ApplicationException ex) {
        return buildResponseEntityForError(ex.getErrorDetail());
    }

    /**
     * Handler for {@link MethodArgumentNotValidException}
     * @param ex the exception
     * @return a {@link ResponseEntity}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> processMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.error("Processing validation error: " + ex.getMessage());
        return processValidationError(ex.getBindingResult());
    }

    /**
     * Convenience method to process the validation errors
     */
    private ResponseEntity<?> processValidationError(final BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        ErrorDetail errorDetail;
        if (fieldErrors.isEmpty()) {
            List<ObjectError> objectErrors = bindingResult.getAllErrors();
            errorDetail = processCustomFieldErrors(objectErrors);
        } else {
            errorDetail = processFieldErrors(fieldErrors);
        }
        return buildResponseEntityForError(errorDetail);
    }

    private ErrorDetail processFieldErrors(final List<FieldError> fieldErrors) {
        List<FieldErrorDto> fieldErrorDtos = fieldErrors.stream()
                .map(fieldError -> new FieldErrorDto(
                        fieldError.getObjectName(),
                        fieldError.getField(),
                        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : fieldError.getCode()))
                .collect(Collectors.toList());

        return new ErrorDetail(ErrorCode.BAD_REQUEST, fieldErrorDtos);
    }

    private ErrorDetail processCustomFieldErrors(final List<ObjectError> customFieldErrors) {
        List<FieldErrorDto> fieldErrorDtos = customFieldErrors.stream()
                .map(objectError -> new FieldErrorDto(
                        objectError.getObjectName(),
                        objectError.getDefaultMessage() != null ? objectError.getDefaultMessage() : objectError.getCode()))
                .collect(Collectors.toList());

        return new ErrorDetail(ErrorCode.BAD_REQUEST, fieldErrorDtos);
    }

    private ResponseEntity<?> buildResponseEntityForError(final ErrorDetail errorDetail) {
        return new ResponseEntity<>(
                translatedErrorDetail(errorDetail),
                HttpStatus.valueOf(errorDetail.getErrorCode().getHttpCode()));
    }

    private ErrorDetail translatedErrorDetail(final ErrorDetail errorDetail) {
        ErrorCode errorCode = errorDetail.getErrorCode();
        String i18nKey = errorCode.getI18nKey();
        errorDetail.setDescription(internationalizationService.getTranslation(i18nKey));
        return errorDetail;
    }
}
