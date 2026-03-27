package com.tekmetric.response.car;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekmetric.response.Visibility;
import java.util.UUID;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
  @JsonView(Visibility.Public.class)
  private UUID id;

  @JsonView(Visibility.Public.class)
  private String carInfo;

  @JsonView(Visibility.Detailed.class)
  private String ownerInfo;
}
