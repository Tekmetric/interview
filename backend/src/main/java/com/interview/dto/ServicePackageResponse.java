package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServicePackageResponse(
    Long id,
    String name,
    String description,
    BigDecimal monthlyPrice,
    Boolean active,
    Integer subscriberCount,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    String createdBy,
    String updatedBy
) {}