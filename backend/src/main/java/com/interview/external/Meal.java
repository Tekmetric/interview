package com.interview.external;

import java.util.Map;


public class Meal {

  private Integer id;

  private String name;

  private Map<String, IngredientProperties> mealIngredients;

  public Meal() {
  }

  public Meal(Integer id, String name, Map<String, IngredientProperties>  mealIngredients) {
    this.id = id;
    this.name = name;
    this.mealIngredients = mealIngredients;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Map<String, IngredientProperties>  getMealIngredients() {
    return mealIngredients;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMealIngredients(
      Map<String, IngredientProperties> mealIngredients) {
    this.mealIngredients = mealIngredients;
  }
}
