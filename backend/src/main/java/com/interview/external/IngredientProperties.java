package com.interview.external;

public class IngredientProperties {

  private UnitOfMeasure units;

  private Double quantity;

  public IngredientProperties() {
  }
  public Double getQuantity() {
    return quantity;
  }

  public UnitOfMeasure getUnits() {
    return units;
  }

  public void setUnits(UnitOfMeasure units) {
    this.units = units;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }
}
