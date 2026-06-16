package com.interview.validation;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.interview.dto.request.CreateCreditApplicationRequest;
import com.interview.repository.CustomerRepository;

@Component
@RequiredArgsConstructor
public class LoanAmountValidator implements ConstraintValidator<ValidLoanAmount, CreateCreditApplicationRequest> {

    private static final BigDecimal INCOME_MULTIPLIER = BigDecimal.valueOf(5);

    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(final CreateCreditApplicationRequest request, final ConstraintValidatorContext context) {
        if (request == null
                || request.getCustomerId() == null
                || request.getRequestedLoanAmount() == null) {
            return true;
        }

        return customerRepository.findById(request.getCustomerId())
                .map(customer -> {
                    final BigDecimal maxAllowed = customer.getEmploymentDetails()
                            .getAnnualIncome()
                            .multiply(INCOME_MULTIPLIER);
                    return request.getRequestedLoanAmount().compareTo(maxAllowed) <= 0;
                })
                .orElse(true);
    }
}
