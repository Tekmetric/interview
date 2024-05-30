package com.interview.conversion.db;

import com.interview.conversion.Converter;
import com.interview.db.meals.Ingredients;
import com.interview.model.meals.dto.IngredientsDto;
import org.springframework.stereotype.Component;

@Component
public class IngredientsDtoConverter implements Converter<Ingredients, IngredientsDto> {

  @Override
  public IngredientsDto forward(Ingredients input) {
    IngredientsDto ingredients = new IngredientsDto(input.getId(), input.getName(), input.getUnits(), input.getQuantity());
    return ingredients;
  }

  @Override
  public Ingredients backward(IngredientsDto input) {
    Ingredients ingredients = new Ingredients(input.getName());
    ingredients.setId(input.getId());
    ingredients.setQuantity(input.getQuantity());
    ingredients.setUnits(input.getUnits());
    return ingredients;
  }
}
