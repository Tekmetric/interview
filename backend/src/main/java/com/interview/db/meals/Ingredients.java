package com.interview.db.meals;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ingredients",
uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "fkMeal"})})
public class Ingredients {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "units")
  @Enumerated(EnumType.STRING)
  private UnitOfMeasure units;

  @Column(name = "quantity")
  private Double quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fkMeal", referencedColumnName = "id")
  private Meal meal;

  protected Ingredients() {
  }

  public Ingredients(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Meal getMeal() {
    return meal;
  }

  public Double getQuantity() {
    return quantity;
  }

  public UnitOfMeasure getUnits() {
    return units;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUnits(UnitOfMeasure units) {
    this.units = units;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public void setMeal(Meal meal) {
    this.meal = meal;
  }
}
