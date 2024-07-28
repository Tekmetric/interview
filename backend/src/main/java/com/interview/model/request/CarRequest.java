package com.interview.model.request;

import com.interview.model.Color;
import com.interview.model.Make;
import com.interview.model.Model;

public class CarRequest {
  private Make make;

  private Model model;

  private int productionYear;

  private Color color;

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
