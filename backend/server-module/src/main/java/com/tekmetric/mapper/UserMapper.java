package com.tekmetric.mapper;

import com.tekmetric.UserModel;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.user.UserResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class UserMapper {
  public static List<UserResponse> toResponses(List<UserModel> userModels) {
    return userModels.stream().map(UserMapper::toResponse).collect(Collectors.toList());
  }

  public static UserResponse toResponse(UserModel user) {
    return UserResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .birthDate(user.getBirthDate())
        .build();
  }

  public static PagedResponse<UserResponse> toPagedResponse(final Page<UserModel> page) {
    return new PagedResponse<>(
        toResponses(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }
}
