package com.interview.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EnrollCustomerRequest {
    @NotNull
    private UUID customerId;

    private UUID rewardsAccountId;
}
