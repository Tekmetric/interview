package com.tekmetric.resource;

import static org.springframework.http.ResponseEntity.status;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekmetric.CarPortal;
import com.tekmetric.mapper.CarMapper;
import com.tekmetric.model.CarFilter;
import com.tekmetric.model.CarUpdates;
import com.tekmetric.request.CarCreationRequest;
import com.tekmetric.request.CarUpdateRequest;
import com.tekmetric.response.ErrorResponse;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.Visibility;
import com.tekmetric.response.car.CarResponse;
import com.tekmetric.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "This collection of operation with car entities")
public class CarResource {

  private final CarPortal carPortal;

  @GetMapping("/{id}")
  @Operation(summary = "Get car by ID", description = "Fetch car by unique identifier")
  @ApiResponse(responseCode = "200", description = "Car retrieved successfully")
  @ApiResponse(
      responseCode = "404",
      description = "Car not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @JsonView(Visibility.Public.class)
  public ResponseEntity<CarResponse> getCarById(
      @RequestParam(name = "Car unique identifier") @PathVariable UUID id) {
    return status(HttpStatus.OK).body(CarMapper.toResponse(carPortal.getById(id)));
  }

  @GetMapping()
  @Operation(summary = "Get cars (paginated) by filter", description = "Fetch cars")
  @ApiResponse(responseCode = "200", description = "Cars retrieved successfully")
  @JsonView(Visibility.Public.class)
  public ResponseEntity<PagedResponse<CarResponse>> getCars(
      @ParameterObject CarFilter filter, @ParameterObject @PageableDefault Pageable pageable) {
    if (filter != null && filter.getYear() != null) {
      ValidationUtil.validateYearFormat(filter.getYear().toString());
    }
    return status(HttpStatus.OK)
        .body(CarMapper.toPagedResponse(carPortal.getCars(filter, pageable)));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete car by ID", description = "Delete car by unique identifier")
  @ApiResponse(responseCode = "204", description = "Car deleted successfully")
  @ApiResponse(responseCode = "404", description = "Car not found")
  @JsonView(Visibility.Public.class)
  public ResponseEntity<CarResponse> deleteCarById(
      @RequestParam(name = "Car unique identifier") @PathVariable UUID id) {
    carPortal.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update car")
  @ApiResponse(responseCode = "200", description = "Cars retrieved successfully")
  @JsonView(Visibility.Public.class)
  public ResponseEntity<CarResponse> updateCar(
      @RequestParam(name = "Car unique identifier") @PathVariable UUID id,
      @RequestBody CarUpdateRequest request) {
    CarUpdates updates =
        CarUpdates.builder().ownerId(request.getOwnerId()).color(request.getColor()).build();
    return status(HttpStatus.OK).body(CarMapper.toResponse(carPortal.update(id, updates)));
  }

  @PostMapping("/createCars")
  @Operation(summary = "Create a new cars")
  @ApiResponse(responseCode = "201", description = "Cars is created")
  @ApiResponse(responseCode = "400", description = "Request validation error")
  @JsonView(Visibility.Detailed.class)
  public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarCreationRequest request)
      throws BadRequestException {
    return status(HttpStatus.CREATED)
        .body(CarMapper.toResponse(carPortal.createCar(CarMapper.toModel(request))));
  }
}
