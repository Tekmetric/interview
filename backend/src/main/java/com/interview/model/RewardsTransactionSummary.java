package com.interview.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class RewardsTransactionSummary {
    @NotNull
    private ZonedDateTime date;
    @NotNull
    private RewardsTransactionType transactionType;
    @NotNull
    private float transactionAmount;
    @NotNull
    private float rewardsRate;
}
