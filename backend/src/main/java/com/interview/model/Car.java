package com.interview.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Car {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Column
  private Make make;

  @Column
  private Model model;

  @Column
  private int productionYear;

  @Column
  private Color color;

  @Column
  String license;

  public Car() {}

  public Car(Make make, Model model, int productionYear, Color color, String license) {
    this.make = make;
    this.model = model;
    this.productionYear = productionYear;
    this.color = color;
    this.license = license;
  }

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

  public void setMake(Make make) {
    this.make = make;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public void setProductionYear(int productionYear) {
    this.productionYear = productionYear;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setLicense(String license) {
    this.license = license;
  }
}
