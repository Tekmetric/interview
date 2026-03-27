package com.tekmetric.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarFilter {
  public OwnerFilter ownerFilter;
  public String make;
  public String model;
  @Min(value = 1000, message = "Year must be a 4-digit value")
  @Max(value = 9999, message = "Year must be a 4-digit value")
  public Integer year;
  public String color;
}
