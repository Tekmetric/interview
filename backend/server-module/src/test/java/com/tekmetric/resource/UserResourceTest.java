package com.tekmetric.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.UserModel;
import com.tekmetric.UserPortal;
import com.tekmetric.UserUpdates;
import com.tekmetric.request.UserUpdateRequest;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.user.UserResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

  @Mock private UserPortal userPortal;

  @InjectMocks private UserResource userResource;

  @Test
  void updateUser_mapsRequestToUserUpdatesAndReturnsResponse() {
    UUID id = UUID.randomUUID();
    LocalDate newBirthDate = LocalDate.of(1990, 5, 12);

    UserUpdateRequest request =
        UserUpdateRequest.builder().email("new.email@example.com").birthDate(newBirthDate).build();

    UserModel updatedModel =
        UserModel.builder()
            .id(id)
            .firstName("John")
            .lastName("Doe")
            .email("new.email@example.com")
            .birthDate(newBirthDate)
            .build();

    when(userPortal.updateUser(eq(id), any(UserUpdates.class))).thenReturn(updatedModel);

    ResponseEntity<UserResponse> response = userResource.updateUser(id, request);

    // Verify mapping to UserUpdates
    ArgumentCaptor<UserUpdates> captor = ArgumentCaptor.forClass(UserUpdates.class);
    verify(userPortal).updateUser(eq(id), captor.capture());
    UserUpdates passed = captor.getValue();

    assertEquals("new.email@example.com", passed.getEmail());
    assertEquals(newBirthDate, passed.getBirthDate());

    // Verify response mapping
    assertEquals(HttpStatus.OK, response.getStatusCode());
    UserResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(id, body.getId());
    assertEquals("John", body.getFirstName());
    assertEquals("Doe", body.getLastName());
    assertEquals("new.email@example.com", body.getEmail());
    assertEquals(newBirthDate, body.getBirthDate());
  }

  @Test
  void getUserById_returnsMappedUserResponseAndOkStatus() {
    UUID id = UUID.randomUUID();
    LocalDate birthDate = LocalDate.of(1985, 3, 21);

    UserModel model =
        UserModel.builder()
            .id(id)
            .firstName("Alice")
            .lastName("Smith")
            .email("alice.smith@example.com")
            .birthDate(birthDate)
            .build();

    when(userPortal.getUserById(id)).thenReturn(model);

    ResponseEntity<UserResponse> response = userResource.getUserById(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    UserResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(id, body.getId());
    assertEquals("Alice", body.getFirstName());
    assertEquals("Smith", body.getLastName());
    assertEquals("alice.smith@example.com", body.getEmail());
    assertEquals(birthDate, body.getBirthDate());

    verify(userPortal).getUserById(id);
  }

  @Test
  void getAllUsers_returnsPagedResponseOfMappedUsersAndOkStatus() {
    Pageable pageable = PageRequest.of(0, 10);

    UUID id1 = UUID.randomUUID();
    LocalDate birthDate1 = LocalDate.of(1992, 7, 15);

    UserModel user1 =
        UserModel.builder()
            .id(id1)
            .firstName("Bob")
            .lastName("Brown")
            .email("bob.brown@example.com")
            .birthDate(birthDate1)
            .build();

    // Mock Page<UserModel> that UserPortal returns
    Page<UserModel> page = new PageImpl<>(List.of(user1), pageable, 1L);

    when(userPortal.getAllUsers(pageable)).thenReturn(page);

    ResponseEntity<PagedResponse<UserResponse>> response = userResource.getAllUsers(pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PagedResponse<UserResponse> body = response.getBody();
    assertNotNull(body);

    // Verify pagination metadata mapped from Page
    assertEquals(0, body.getPage());
    assertEquals(10, body.getSize());
    assertEquals(1L, body.getTotalElements());
    assertEquals(1, body.getTotalPages());
    assertTrue(body.isFirst());
    assertTrue(body.isLast());

    // Verify content mapping
    assertNotNull(body.getContent());
    assertEquals(1, body.getContent().size());

    UserResponse first = body.getContent().get(0);
    assertEquals(id1, first.getId());
    assertEquals("Bob", first.getFirstName());
    assertEquals("Brown", first.getLastName());
    assertEquals("bob.brown@example.com", first.getEmail());
    assertEquals(birthDate1, first.getBirthDate());

    verify(userPortal).getAllUsers(pageable);
  }
}
