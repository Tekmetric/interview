package com.interview.conversion.db;

import com.interview.conversion.Converter;
import com.interview.db.meals.Ingredients;
import com.interview.db.meals.Meal;
import com.interview.model.meals.dto.IngredientsDto;
import com.interview.model.meals.dto.MealDto;
import com.interview.utils.CollectionHelper;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MealsDtoConverter implements Converter<Meal, MealDto> {

  @Autowired
  private UserDtoConverter userConverter;

  @Autowired
  private IngredientsDtoConverter ingredientConverter;


  @Override
  public MealDto forward(Meal input) {
    final MealDto dto = new MealDto(input.getId(), userConverter.forward(input.getUser()),
        input.getName(),
        ingredientsDto(input.getIngredients()));
    return dto;
  }

  private Set<IngredientsDto> ingredientsDto(Set<Ingredients> ingredients) {
    return CollectionHelper.convertSet(ingredients, ingredientConverter::forward);
  }

  private Set<Ingredients> ingredients(Set<IngredientsDto> ingredientsDtos, Meal meal) {
    return CollectionHelper.convertSet(ingredientsDtos, dto -> {
      Ingredients ingredients = ingredientConverter.backward(dto);
      ingredients.setMeal(meal);
      return ingredients;
    });
  }

  @Override
  public Meal backward(MealDto input) {
    final Meal meal = new Meal(input.getId(), userConverter.backward(input.getUser()),
        input.getName(), null);
    Set<Ingredients> ingredients = ingredients(input.getIngredients(), meal);
    meal.setIngredients(ingredients);
    return meal;
  }
}
