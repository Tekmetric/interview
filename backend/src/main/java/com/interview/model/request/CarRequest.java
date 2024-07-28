package com.interview.model.request;

import com.interview.model.Color;
import com.interview.model.Make;
import com.interview.model.Model;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CarRequest {
  @NotNull
  private Make make;

  @NotNull
  private Model model;

  @NotNull
  @Min(value = 1900, message = "productionYear must greater than 1886")
  private int productionYear;

  @NotNull
  private Color color;

  @NotNull
  @Size(min = 7, message = "license must be of length 7")
  @Size(max = 7, message = "license must be of length 7")
  String license;

  public Make getMake() {
    return make;
  }

  public Model getModel() {
    return model;
  }

  public int getProductionYear() {
    return productionYear;
  }

  public Color getColor() {
    return color;
  }

  public String getLicense() {
    return license;
  }
}
