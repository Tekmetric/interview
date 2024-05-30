package com.interview.model.meals.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link com.interview.db.meals.Meal}
 */
public class MealDto implements Serializable {

  private Integer id;
  private UserDto user;
  private String name;
  private Set<IngredientsDto> ingredients;

  protected MealDto() {
  }
  public MealDto(Integer id, UserDto user, String name, Set<IngredientsDto> ingredients) {
    this.id = id;
    this.user = user;
    this.name = name;
    this.ingredients = ingredients;
  }

  public Integer getId() {
    return id;
  }

  public UserDto getUser() {
    return user;
  }

  public String getName() {
    return name;
  }

  public Set<IngredientsDto> getIngredients() {
    return ingredients;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MealDto entity = (MealDto) o;
    return Objects.equals(this.id, entity.id) &&
        Objects.equals(this.user, entity.user) &&
        Objects.equals(this.name, entity.name) &&
        Objects.equals(this.ingredients, entity.ingredients);
  }

  public void setUser(UserDto user) {
    this.user = user;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, name, ingredients);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "user = " + user + ", " +
        "name = " + name + ", " +
        "ingredients = " + ingredients + ")";
  }
}