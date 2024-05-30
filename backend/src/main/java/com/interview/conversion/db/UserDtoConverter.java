package com.interview.conversion.db;

import com.interview.conversion.Converter;
import com.interview.db.User;
import com.interview.model.meals.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter implements Converter<User, UserDto> {

  @Override
  public UserDto forward(User input) {
    if (input == null) {
      return null;
    }
    return new UserDto(input.getId(), input.getUsername(), input.getFirst(), input.getLast());
  }

  @Override
  public User backward(UserDto input) {
    if (input == null) {
      return null;
    }
    return new User(input.getUsername(), input.getFirst(), input.getLast());
  }
}
