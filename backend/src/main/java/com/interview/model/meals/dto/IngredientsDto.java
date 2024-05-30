package com.interview.model.meals.dto;

import com.interview.db.meals.UnitOfMeasure;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link com.interview.db.meals.Ingredients}
 */
public class IngredientsDto implements Serializable {

  private Integer id;
  private String name;
  private UnitOfMeasure units;
  private Double quantity;

  protected IngredientsDto() {
  }

  public IngredientsDto(Integer id, String name, UnitOfMeasure units, Double quantity) {
    this.id = id;
    this.name = name;
    this.units = units;
    this.quantity = quantity;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UnitOfMeasure getUnits() {
    return units;
  }

  public Double getQuantity() {
    return quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IngredientsDto entity = (IngredientsDto) o;
    return Objects.equals(this.id, entity.id) &&
        Objects.equals(this.name, entity.name) &&
        Objects.equals(this.units, entity.units) &&
        Objects.equals(this.quantity, entity.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, units, quantity);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "name = " + name + ", " +
        "units = " + units + ", " +
        "quantity = " + quantity + ")";
  }
}