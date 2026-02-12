package com.interview.model.dto.response;

import com.interview.model.common.Status;
import java.util.UUID;
import lombok.Data;

@Data
public class OfferDto {
  private UUID offerId;
  private Double offerPrice;
  private String lenderName;
  private Status status;
}
