package com.interview.components;

import com.interview.db.meals.IngredientsRepository;
import com.interview.db.meals.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class MealRepoComponent {

  @Autowired
  private IngredientsRepository ingredientsRepository;

  @Autowired
  private MealRepository mealRepository;

  @Bean
  @Scope("singleton")
  public IngredientsRepository getIngredientsRepository() {
    return ingredientsRepository;
  }

  @Bean
  @Scope("singleton")
  public MealRepository getMealRepository() {
    return mealRepository;
  }
}
