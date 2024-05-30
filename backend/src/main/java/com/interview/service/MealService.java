package com.interview.service;

import com.interview.components.MealRepoComponent;
import com.interview.components.UserRepoComponent;
import com.interview.conversion.db.IngredientsDtoConverter;
import com.interview.conversion.db.MealsDtoConverter;
import com.interview.conversion.db.UserDtoConverter;
import com.interview.db.User;
import com.interview.db.UserRepository;
import com.interview.db.meals.Ingredients;
import com.interview.db.meals.IngredientsRepository;
import com.interview.db.meals.Meal;
import com.interview.db.meals.MealRepository;
import com.interview.exceptions.InvalidInputException;
import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UnauthorizedException;
import com.interview.model.meals.dto.IngredientsDto;
import com.interview.model.meals.dto.MealDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealService {

  @Autowired
  private MealsDtoConverter mealDtoConverter;

  @Autowired
  private IngredientsDtoConverter ingredientsDtoConverter;

  @Autowired
  private MealRepoComponent mealRepoComponent;

  @Autowired
  private UserDtoConverter userDtoConverter;

  @Autowired
  private UserRepoComponent userRepoComponent;

  private static final Logger LOG = LogManager.getLogger(MealService.class);

  public MealService() {
  }

  public Integer validateUser_AndReturnId(String userName) {
    final User user = validateUser(userName);
     return user.getId();
  }


  public List<MealDto> findMeals(Integer userId) {
      List<Meal> meals = mealRepository().findByUser_Id(userId);
      return meals.stream()
        .map(mealDtoConverter::forward)
        .collect(Collectors.toList());
  }

  public MealDto findMeal(Integer userId, String mealName) {
    Meal meal = getMeal(userId, mealName);
    if (meal == null) {
      throw new NotFoundException();
    }
    return mealDtoConverter.forward(meal);
  }


  public void deleteMeal(Integer userId, String mealName) {
    final Meal meal = mealRepository().findByNameAndUser_Id(mealName, userId);
    if (meal == null) {
      throw new NotFoundException();
    }
    if (meal.getIngredients() != null) {
      ingredientRepository().deleteAll(meal.getIngredients());
    }
    meal.setUser(null);
    mealRepository().delete(meal);
  }

  public void createMeal(Integer userId, MealDto mealDto) {
    try {
      final Optional<User> user = userRepository().findById(userId);
      if (!user.isPresent()) {
        throw new InvalidInputException("userName");
      }
      final Meal meal = mealDtoConverter.backward(mealDto);
      meal.setUser(user.get());
      mealRepository().save(meal);
      if (meal.getIngredients() != null) {
        createIngredients(meal, mealDto);
      }
    } catch (ConstraintViolationException e) {
        throw new InvalidInputException("Could not create duplicate meal.");
    }
  }

  public void updateMeal(Integer userId, String mealName, MealDto mealDto) {
    if (!Objects.equals(mealName, mealDto.getName())) {
      throw new InvalidInputException("Meal name mismatch");
    }
    Meal internalMeal = getMeal(userId, mealName);
    if (internalMeal == null) {
      throw new NotFoundException();
    }
    Meal meal = mealDtoConverter.backward(mealDto);
    meal.setUser(internalMeal.getUser());
    meal.setId(internalMeal.getId());
    meal.setIngredients(updatedIngredients(internalMeal, mealDto));
    mealRepository().save(meal);
  }

  private User validateUser(String userName) {
    final User user = userRepository().findByUsername(userName);
    if (user == null) {
      throw new UnauthorizedException();
    }
    return user;
  }

  private void createIngredients(Meal meal, MealDto mealDto) {
    final Iterable<Ingredients> ingredients = mealDto.getIngredients().stream()
        .map(element ->{  final Ingredients ingredient = ingredientsDtoConverter.backward(element);
          ingredient.setMeal(meal);
          return ingredient;
        })::iterator;
    ingredientRepository().saveAll(ingredients);
  }


  private Set<Ingredients> updatedIngredients(Meal meal, MealDto mealDto) {
    Map<String, IngredientsDto> inputMap = mealDto.getIngredients().stream()
        .collect(Collectors.toMap(IngredientsDto::getName, Function.identity()));

    List<Ingredients> toDelete = new ArrayList<>();
    List<Ingredients> toUpdate = new ArrayList<>();
    Set<Ingredients> output = new HashSet<>();
    LOG.debug("Found ingredients: {}", meal.getIngredients().size());
    for (Ingredients ingredient : meal.getIngredients()) {
      LOG.debug("Checking ingredient: {}", ingredient.getName());
      IngredientsDto matchedIngredient = inputMap.get(ingredient.getName());
      // Delete
      if (matchedIngredient == null) {
        LOG.debug("Deleting ingredient: {}", ingredient.getName());
        toDelete.add(ingredient);
        continue;
      }
      inputMap.remove(ingredient.getName());
      // NOTE: an optimization can be made here to avoid persisting based on
      // a partial match of fields.  Since this is not trivial without reflection,
      // we will skip it for this demo's purposes.
      LOG.debug("Updating ingredient: {}", ingredient.getName());
      Ingredients converted = ingredientsDtoConverter.backward(matchedIngredient);
      ingredient.setUnits(converted.getUnits());
      ingredient.setQuantity(converted.getQuantity());
      // Persist all IDs and FKs
      toUpdate.add(ingredient);
    }
    for (Map.Entry<String, IngredientsDto> entry : inputMap.entrySet()) {
      LOG.debug("Inserting ingredient {}", entry.getKey());
      Ingredients ingredient = ingredientsDtoConverter.backward(entry.getValue());
      ingredient.setName(entry.getKey());
      ingredient.setMeal(meal);
      toUpdate.add(ingredient);
    }
    LOG.debug(() -> "Deleting " + toDelete.stream().map(Ingredients::getName)
        .collect(Collectors.joining(",")));
    ingredientRepository().deleteAll(toDelete);
    LOG.debug(() ->
        "Saving " + toUpdate.stream().map(Ingredients::getName).collect(Collectors.joining(",")));

    ingredientRepository().saveAll(toUpdate);
    return output;
  }

  private Meal getMeal(Integer userId, String mealName) {
     return mealRepository().findByNameAndUser_Id(mealName, userId);
  }

  private MealRepository mealRepository() {
    return mealRepoComponent.getMealRepository();
  }


  private IngredientsRepository ingredientRepository() {
    return mealRepoComponent.getIngredientsRepository();
  }

  private UserRepository userRepository() {
    return userRepoComponent.getUserRepository();
  }
}
