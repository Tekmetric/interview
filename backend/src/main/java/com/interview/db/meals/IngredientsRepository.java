package com.interview.db.meals;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface IngredientsRepository extends CrudRepository<Ingredients, Integer> {

  List<Ingredients> findByMeal_Id(Integer id);
}
