package com.tekmetric.repository;

import com.tekmetric.UserModel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public static List<UserModel> toModels(List<User> users) {
    return users.stream().map(UserMapper::toModel).collect(Collectors.toList());
  }

  public static UserModel toModel(User user) {
    return UserModel.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .birthDate(user.getBirthDate())
        .build();
  }

  public static List<User> toEntities(List<UserModel> userModels) {
    return userModels.stream().map(UserMapper::toEntity).collect(Collectors.toList());
  }

  public static User toEntity(UserModel userModel) {
    return User.builder()
        .id(userModel.getId())
        .firstName(userModel.getFirstName())
        .lastName(userModel.getLastName())
        .email(userModel.getEmail())
        .birthDate(userModel.getBirthDate())
        .build();
  }
}
