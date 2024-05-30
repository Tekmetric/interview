package com.interview.db.meals;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MealRepository extends CrudRepository<Meal, Integer> {


  List<Meal> findByUser_Username(String username);

  List<Meal> findByUser_Id(Integer id);

  Meal findByNameAndUser_Id(String name, Integer id);

}
