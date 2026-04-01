package com.interview.model.dto.request;

import com.interview.model.common.Status;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OfferPatchDto {
  @Positive private Double offerPrice;
  private String lenderName;
  private Status status;
}
