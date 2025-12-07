package com.tekmetric.validation;

import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.repository.User;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class UserValidationUtil {
  public static void validateUserList(List<UUID> ids, List<User> fetchUsers) {
    if (fetchUsers.size() != ids.size()) {
      List<UUID> fetchedUserIds = fetchUsers.stream().map(User::getId).toList();
      List<UUID> missing = ids.stream().filter(id -> !fetchedUserIds.contains(id)).toList();
      throw new UserNotFoundException("User(s) not found for IDs: " + missing);
    }
  }
}
