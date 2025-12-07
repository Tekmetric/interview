package com.tekmetric;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.repository.User;
import com.tekmetric.repository.UserDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class UserPortalImplTest {

  @Mock private UserDAO userDAO;

  @InjectMocks private UserPortalImpl userPortal;

  @Test
  void getUserById_userExists_returnsModel() {
    UUID id = UUID.randomUUID();
    User entity =
        User.builder()
            .id(id)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    when(userDAO.findById(id)).thenReturn(Optional.of(entity));

    UserModel result = userPortal.getUserById(id);

    assertEquals(id, result.getId());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("john.doe@example.com", result.getEmail());
  }

  @Test
  void getUserById_userMissing_throwsUserNotFoundException() {
    UUID id = UUID.randomUUID();
    when(userDAO.findById(id)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userPortal.getUserById(id));
  }

  @Test
  void getUsersById_allFound_returnsMappedModels() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    List<UUID> ids = List.of(id1, id2);
    List<User> entities =
        List.of(
            User.builder().id(id1).firstName("A").lastName("B").build(),
            User.builder().id(id2).firstName("C").lastName("D").build());

    when(userDAO.findAllById(ids)).thenReturn(entities);

    List<UserModel> result = userPortal.getUsersById(ids);

    assertEquals(2, result.size());
    assertEquals(id1, result.get(0).getId());
    assertEquals(id2, result.get(1).getId());
  }

  @Test
  void getAllUsers_returnsPageOfUserModels() {
    Pageable pageable = PageRequest.of(0, 10);
    User entity = User.builder().id(UUID.randomUUID()).firstName("John").lastName("Doe").build();
    Page<User> page = new PageImpl<>(List.of(entity), pageable, 1);

    when(userDAO.findAll(pageable)).thenReturn(page);

    Page<UserModel> result = userPortal.getAllUsers(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("John", result.getContent().get(0).getFirstName());
  }

  @Test
  void updateUser_updatesEmailAndBirthDateAndSaves() {
    UUID id = UUID.randomUUID();
    User entity =
        User.builder()
            .id(id)
            .firstName("John")
            .lastName("Doe")
            .email("old@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    when(userDAO.findById(id)).thenReturn(Optional.of(entity));
    when(userDAO.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0, User.class));

    UserUpdates updates =
        UserUpdates.builder().email("new@example.com").birthDate(LocalDate.of(1991, 2, 2)).build();

    UserModel result = userPortal.updateUser(id, updates);

    assertEquals("new@example.com", result.getEmail());
    assertEquals(LocalDate.of(1991, 2, 2), result.getBirthDate());
    verify(userDAO).save(any(User.class));
  }
}
