package com.tekmetric.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.repository.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserValidationUtilTest {

  @Test
  void validateUserList_allIdsFetched_doesNotThrow() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    List<UUID> ids = List.of(id1, id2);
    List<User> fetched = List.of(User.builder().id(id1).build(), User.builder().id(id2).build());

    assertDoesNotThrow(() -> UserValidationUtil.validateUserList(ids, fetched));
  }

  @Test
  void validateUserList_missingUsers_throwsUserNotFoundException() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    List<UUID> ids = List.of(id1, id2);
    List<User> fetched = List.of(User.builder().id(id1).build());

    UserNotFoundException ex =
        assertThrows(
            UserNotFoundException.class, () -> UserValidationUtil.validateUserList(ids, fetched));

    assertTrue(ex.getMessage().contains("User(s) not found for IDs"));
    assertTrue(ex.getMessage().contains(id2.toString()));
  }
}
