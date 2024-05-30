package com.interview.configuration;


import com.interview.conversion.rest.RestIngredientsConverter;
import com.interview.conversion.rest.RestMealConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestMealConfiguration {

  @Bean
  RestIngredientsConverter restIngredientsConverter() {
    return new RestIngredientsConverter();
  }
  @Bean
  RestMealConverter restMealConverter(RestIngredientsConverter ingredientsConverter) {
    return new RestMealConverter(ingredientsConverter);
  }

}
