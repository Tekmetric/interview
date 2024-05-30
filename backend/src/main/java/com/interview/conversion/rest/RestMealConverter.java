package com.interview.conversion.rest;

import com.interview.conversion.Converter;
import com.interview.external.IngredientProperties;
import com.interview.external.Meal;
import com.interview.model.meals.dto.IngredientsDto;
import com.interview.model.meals.dto.MealDto;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RestMealConverter implements Converter<Meal, MealDto> {

  private RestIngredientsConverter ingredientsConverter;
  public RestMealConverter(RestIngredientsConverter ingredientsConverter) {
    this.ingredientsConverter = ingredientsConverter;
  }

  @Override
  public MealDto forward(Meal input) {
    MealDto output = new MealDto(null, null, input.getName(), forwardConvertIngredients(input));
    return output;
  }

  private Set<IngredientsDto> forwardConvertIngredients(Meal meal) {
    if (meal.getMealIngredients() == null) {
      return null;
    }
    return meal.getMealIngredients().entrySet()
        .stream().map(
            ingredientsConverter::forward
        ).collect(Collectors.toSet());
  }

  private Map<String, IngredientProperties> backwardsConvertIngredients(MealDto meal) {
    if (meal.getIngredients() == null) {
      return null;
    }
    return meal.getIngredients().stream().map(ingredientsConverter::backward)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public Meal backward(MealDto input) {
    Meal output = new Meal();
    output.setId(input.getId());
    output.setName(input.getName());
    output.setMealIngredients(backwardsConvertIngredients(input));
    return output;
  }
}
