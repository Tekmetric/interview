package com.interview.transactions.api.model;

import com.interview.transactions.service.dto.Currency;
import com.interview.transactions.service.dto.Status;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateTransactionRequest {

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
    BigDecimal amount;

    @NotNull(message = "currency is required")
    private Currency currency;

    @NotNull(message = "status is required")
    private Status status;
}
