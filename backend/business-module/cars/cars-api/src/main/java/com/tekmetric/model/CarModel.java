package com.tekmetric.model;

import com.tekmetric.UserModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CarModel {
  @NotNull private final UUID id;
  @NotNull private UserModel owner;
  @NotNull @NotBlank private String make;
  @NotNull @NotBlank private String model;

  // integer in H2
  @NotNull private Year manufactureYear;

  private String color;

  public String getCarInfo() {
    return make + " " + model + " " + manufactureYear + " " + color + " color.";
  }

  public String getUserInfo() {
    return owner == null ? null : owner.getFirstName() + " " + owner.getLastName();
  }
}
