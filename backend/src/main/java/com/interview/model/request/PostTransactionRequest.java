package com.interview.model.request;

import com.interview.model.RewardsTransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PostTransactionRequest {

    @NotNull
    private float amount;

    @NotNull
    private UUID rewardsAccountId;

    @NotNull
    private RewardsTransactionType type;

    @NotNull
    private UUID purchaseId;



}
