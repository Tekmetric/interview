package com.tekmetric.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekmetric.UserModel;
import com.tekmetric.UserPortal;
import com.tekmetric.UserUpdates;
import com.tekmetric.mapper.UserMapper;
import com.tekmetric.request.UserUpdateRequest;
import com.tekmetric.response.ErrorResponse;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.Visibility;
import com.tekmetric.response.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management API")
public class UserResource {
  private final UserPortal userPortal;

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Fetch user by unique identifier")
  @ApiResponse(responseCode = "200", description = "User retrieved successfully")
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @JsonView(Visibility.Public.class)
  public ResponseEntity<UserResponse> getUserById(
      @RequestParam(name = "User unique identifier") @PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(UserMapper.toResponse(userPortal.getUserById(id)));
  }

  @GetMapping()
  @Operation(summary = "Get all users (paginated)", description = "Fetch all user")
  @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
  @JsonView(Visibility.Public.class)
  public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
      @ParameterObject @PageableDefault Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(UserMapper.toPagedResponse(userPortal.getAllUsers(pageable)));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update user's email and birth date")
  @ApiResponse(responseCode = "200", description = "User updated successfully")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @ApiResponse(responseCode = "404", description = "User not found")
  @JsonView(Visibility.Public.class)
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id, @RequestBody UserUpdateRequest request) {
    UserUpdates updates =
        UserUpdates.builder().email(request.getEmail()).birthDate(request.getBirthDate()).build();
    UserModel updated = userPortal.updateUser(id, updates);
    return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toResponse(updated));
  }
}
