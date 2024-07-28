package com.interview.model.response;

import com.interview.model.Car;
import com.interview.model.Color;
import com.interview.model.Make;
import com.interview.model.Model;

public class CarResponse {
  private Make make;

  private Model model;

  private int productionYear;

  private Color color;

  String license;

  public CarResponse(Make make, Model model, int productionYear, Color color, String license) {
    this.make = make;
    this.model = model;
    this.productionYear = productionYear;
    this.color = color;
    this.license = license;
  }

  public CarResponse(Car car) {
    this.make = car.getMake();
    this.model = car.getModel();
    this.productionYear = car.getProductionYear();
    this.color = car.getColor();
    this.license = car.getLicense();
  }

  public Make getMake() {
    return make;
  }

  public void setMake(Make make) {
    this.make = make;
  }

  public Model getModel() {
    return model;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public int getProductionYear() {
    return productionYear;
  }

  public void setProductionYear(int productionYear) {
    this.productionYear = productionYear;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }
}
