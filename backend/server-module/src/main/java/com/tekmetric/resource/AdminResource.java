package com.tekmetric.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekmetric.CarPortal;
import com.tekmetric.UserPortal;
import com.tekmetric.mapper.CarMapper;
import com.tekmetric.mapper.UserMapper;
import com.tekmetric.request.CarBulkCreationRequest;
import com.tekmetric.request.CarCreationRequest;
import com.tekmetric.response.*;
import com.tekmetric.response.Visibility;
import com.tekmetric.response.car.CarBulkCreationResponse;
import com.tekmetric.response.car.CarCreationError;
import com.tekmetric.response.car.CarResponse;
import com.tekmetric.response.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(
    name = "Admin",
    description =
        "This collection of functions that serve as external management tool outside of main business logic")
public class AdminResource {
  private final UserPortal userPortal;
  private final CarPortal carPortal;

  // TODO: Add role check. Only users with member access have rights to trigger this API
  @GetMapping("user/{id}")
  @Operation(summary = "Get user by ID", description = "Fetch user by unique identifier")
  @ApiResponse(responseCode = "200", description = "User retrieved successfully")
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @JsonView(Visibility.Detailed.class)
  public ResponseEntity<UserResponse> getUserById(
      @RequestParam(name = "User unique identifier") @PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(UserMapper.toResponse(userPortal.getUserById(id)));
  }

  @PostMapping("/addCars")
  @Operation(summary = "Create multiple cars in bulk")
  @ApiResponse(responseCode = "201", description = "Bulk create processed")
  public ResponseEntity<CarBulkCreationResponse> createCars(
      @RequestBody CarBulkCreationRequest bulkRequest) {
    List<CarResponse> successes = new ArrayList<>();
    List<CarCreationError> failures = new ArrayList<>();

    var items =
        bulkRequest.getCars() != null ? bulkRequest.getCars() : List.<CarCreationRequest>of();

    for (int i = 0; i < items.size(); i++) {
      CarCreationRequest item = items.get(i);
      try {
        var created = carPortal.createCar(CarMapper.toModel(item));
        successes.add(CarMapper.toResponse(created));
      } catch (BadRequestException ex) {
        failures.add(new CarCreationError(i, ex.getMessage()));
      } catch (RuntimeException ex) {
        // Catch any unexpected errors as failures too
        failures.add(new CarCreationError(i, "Unexpected error: " + ex.getMessage()));
      }
    }

    CarBulkCreationResponse result =
        new CarBulkCreationResponse(successes.size(), failures.size(), successes, failures);

    var status = failures.isEmpty() ? HttpStatus.CREATED : HttpStatus.MULTI_STATUS;

    return ResponseEntity.status(status).body(result);
  }
}
