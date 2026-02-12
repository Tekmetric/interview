package com.interview.model.dto.request;

import com.interview.model.common.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OfferCreationDto {
  @NotNull @Positive private Double offerPrice;
  @NotEmpty private String lenderName;
  @NotNull private Status status;
}
