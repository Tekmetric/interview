package com.interview.api;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.dto.car.CarUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Car", description = "CRUD operations for Car")
@RequestMapping("/cars")
public interface CarApi {

  @Operation(
      summary = "Get car by ID",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Car found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @GetMapping("/{id}")
  ResponseEntity<CarDTO> getCarById(
      @Parameter(description = "ID of the car") @PathVariable final Long id);

  @Operation(
      summary = "Get all cars (paginated)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of cars",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
      })
  @GetMapping
  ResponseEntity<PageResponseDTO<CarDTO>> getCars(
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")
          final int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") final int size,
      @Parameter(description = "Fuzzy search query") @RequestParam(required = false)
          final String query);

  @Operation(
      summary = "Create a new car",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Car created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
      })
  @PostMapping
  ResponseEntity<CarDTO> createCar(
      @RequestBody(
              description = "Car to create",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = CarCreateRequestDTO.class)))
          @org.springframework.web.bind.annotation.RequestBody
          @NotNull
          @Valid
          final CarCreateRequestDTO car);

  @Operation(
      summary = "Delete car by ID",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Car deleted",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @DeleteMapping("/{id}")
  ResponseEntity<CarDTO> deleteCarById(
      @Parameter(description = "ID of the car") @PathVariable final Long id);

  @Operation(
      summary = "Update an existing car",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Car updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "404", description = "Car not found")
      })
  @PutMapping("/{id}")
  ResponseEntity<CarDTO> updateCar(
      @Parameter(description = "ID of the car") @PathVariable final Long id,
      @org.springframework.web.bind.annotation.RequestBody @NotNull @Valid
          final CarUpdateRequestDTO request);
}
