package com.tekmetric.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarUpdates {
  private UUID ownerId;

  private String color;
}
