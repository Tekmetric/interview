package com.interview.model.dto.request;

import com.interview.model.common.PropertyType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ListingPatchDto {
  private String address;
  private String agentName;
  private PropertyType propertyType;
  @Positive() private Double listingPrice;
}
