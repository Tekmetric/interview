package com.interview.model.dto.request;

import com.interview.model.common.PropertyType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ListingCreationDto {
  @NotEmpty() private String address;
  @NotEmpty() private String agentName;
  @NotNull() private PropertyType propertyType;
  @NotNull() @Positive() private Double listingPrice;
}
