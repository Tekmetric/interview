package com.interview.conversion.rest;

import com.interview.conversion.Converter;
import com.interview.exceptions.ServiceException;
import com.interview.external.IngredientProperties;
import com.interview.external.UnitOfMeasure;
import com.interview.model.meals.dto.IngredientsDto;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class RestIngredientsConverter implements Converter<Map.Entry<String, IngredientProperties>, IngredientsDto> {

  @Override
  public IngredientsDto forward(Map.Entry<String, IngredientProperties> input) {
    final IngredientsDto dto = new IngredientsDto(null, input.getKey(),
        unitOfMeasure(input.getValue().getUnits()), input.getValue()
        .getQuantity());
    return dto;
  }

  private com.interview.db.meals.UnitOfMeasure unitOfMeasure(UnitOfMeasure unitOfMeasure) {
    com.interview.db.meals.UnitOfMeasure converted = com.interview.db.meals.UnitOfMeasure.valueOf(unitOfMeasure.name());
    return converted;
  }

  @Override
  public Map.Entry<String, IngredientProperties> backward(IngredientsDto input) {
    final IngredientProperties properties = new IngredientProperties();
    properties.setQuantity(input.getQuantity());
    final UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(input.getUnits().name());
    if (unitOfMeasure == null) {
      throw new ServiceException("Cannot handle unit of measure.");
    }
    properties.setUnits(unitOfMeasure);
    return Pair.of(input.getName(), properties);
  }


}
