package com.interview.resource;

import com.interview.conversion.rest.RestMealConverter;
import com.interview.external.Meal;
import com.interview.model.meals.dto.MealDto;
import com.interview.service.MealService;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MealResource {

  @Autowired
  private MealService mealService;

  @Autowired
  private RestMealConverter restMealConverter;

  private static final Logger LOG = LogManager.getLogger(MealResource.class);

  @GetMapping("/api/meals")
  public List<Meal> getMeals(@RequestHeader("userName") String userName) {
    LOG.info("Fetching meals for userName {}", userName);
    Integer user = mealService.validateUser_AndReturnId(userName);
    final List<MealDto> meals = mealService.findMeals(user);
    LOG.info("Found meals: {}", meals.size());
    return meals.stream()
        .map(restMealConverter::backward)
        .collect(Collectors.toList());
  }

  @GetMapping("/api/meals/{mealName}")
  public Meal getMeal(@RequestHeader("userName") String userName, @PathVariable("mealName") String mealName) {
    LOG.info("Fetching meal {} for userName {}", mealName, userName);
    Integer userId = mealService.validateUser_AndReturnId(userName);
    MealDto meal = mealService.findMeal(userId, mealName);
    return restMealConverter.backward(meal);
  }

  @PostMapping("/api/meals")
  public void createMeal(@RequestHeader("userName") String userName, @RequestBody Meal meal) {
    LOG.info("Creating meal {} for userName {}", meal.getName(), userName);
    Integer userId = mealService.validateUser_AndReturnId(userName);
    final MealDto mealDto = restMealConverter.forward(meal);
    mealService.createMeal(userId, mealDto);
  }

  @DeleteMapping("/api/meals/{mealName}")
  public void deleteMeal(@RequestHeader("userName") String userName, @PathVariable("mealName") String mealName) {
    LOG.info("Deleting meal {} for userName {}", mealName, userName);
    Integer userId = mealService.validateUser_AndReturnId(userName);
    mealService.deleteMeal(userId, mealName);
  }

  @PutMapping("/api/meals/{mealName}")
  public void updateMeal(@RequestHeader("userName") String userName, @PathVariable("mealName") String mealName, @RequestBody Meal meal) {
    LOG.info("Updating meal {} for userName {}", mealName, userName);
    Integer userId = mealService.validateUser_AndReturnId(userName);
    final MealDto mealDto = restMealConverter.forward(meal);
    mealService.updateMeal(userId, mealName, mealDto);
  }
}
