package com.interview.api;

import com.interview.dto.CarRequest;
import com.interview.dto.CarResponse;
import com.interview.dto.ErrorResponse;
import com.interview.model.CarStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Tag(name = "Car", description = "Car management APIs")
public interface CarApi {

    @Operation(summary = "Get a car by ID", description = "Retrieves a car by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class),
                            examples = @ExampleObject(name = "success", value = """
                                    {
                                      "id": 1,
                                      "vin": "1HGBH41JXMN109186",
                                      "brand": "Honda",
                                      "model": "Civic",
                                      "manufacturedYear": 2023,
                                      "color": "Blue",
                                      "fuelType": "GASOLINE",
                                      "transmission": "Automatic",
                                      "basePrice": 25000.00,
                                      "sellingPrice": null,
                                      "status": "AVAILABLE",
                                      "createdAt": "2024-01-01T10:00:00",
                                      "updatedAt": "2024-01-01T10:00:00"
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "not-found", value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Car not found with id: 99",
                                      "fieldErrors": null,
                                      "timestamp": "2024-01-01T10:00:00"
                                    }""")))
    })
    ResponseEntity<CarResponse> getById(
            @Parameter(description = "Car ID") @PathVariable Long id);

    @Operation(summary = "Get all cars", description = "Retrieves a paginated list of cars with optional filtering by status, brand, and price range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cars retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "success", value = """
                                    {
                                      "content": [
                                        {
                                          "id": 1,
                                          "vin": "1HGBH41JXMN109186",
                                          "brand": "Honda",
                                          "model": "Civic",
                                          "manufacturedYear": 2023,
                                          "color": "Blue",
                                          "fuelType": "GASOLINE",
                                          "transmission": "Automatic",
                                          "basePrice": 25000.00,
                                          "sellingPrice": null,
                                          "status": "AVAILABLE"
                                        }
                                      ],
                                      "totalElements": 5,
                                      "totalPages": 1,
                                      "size": 20,
                                      "number": 0
                                    }""")))
    })
    ResponseEntity<Page<CarResponse>> getAll(
            @Parameter(description = "Filter by car status") @RequestParam(required = false) CarStatus status,
            @Parameter(description = "Filter by brand") @RequestParam(required = false) String brand,
            @Parameter(description = "Minimum base price (inclusive)") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum base price (inclusive)") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based, default 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (default 20)") @RequestParam(defaultValue = "20") int pageSize);

    @Operation(summary = "Create a new car", description = "Creates a new car entry in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Car created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class),
                            examples = @ExampleObject(name = "created", value = """
                                    {
                                      "id": 6,
                                      "vin": "NEW1234567890",
                                      "brand": "Toyota",
                                      "model": "Camry",
                                      "manufacturedYear": 2024,
                                      "color": "White",
                                      "fuelType": "HYBRID",
                                      "transmission": "Automatic",
                                      "basePrice": 35000.00,
                                      "sellingPrice": null,
                                      "status": "AVAILABLE",
                                      "createdAt": "2024-01-01T10:00:00",
                                      "updatedAt": "2024-01-01T10:00:00"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "validation-error", value = """
                                            {
                                              "status": 400,
                                              "error": "Validation Failed",
                                              "message": "One or more fields have validation errors",
                                              "fieldErrors": {
                                                "vin": "VIN is required",
                                                "brand": "Brand is required"
                                              },
                                              "timestamp": "2024-01-01T10:00:00"
                                            }"""),
                                    @ExampleObject(name = "business-rule-error", value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Selling price is required when status is RESERVED",
                                              "fieldErrors": null,
                                              "timestamp": "2024-01-01T10:00:00"
                                            }""")
                            })),
            @ApiResponse(responseCode = "409", description = "Duplicate VIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "duplicate-vin", value = """
                                    {
                                      "status": 409,
                                      "error": "Conflict",
                                      "message": "A car with VIN '1HGBH41JXMN109186' already exists",
                                      "fieldErrors": null,
                                      "timestamp": "2024-01-01T10:00:00"
                                    }""")))
    })
    ResponseEntity<CarResponse> create(
            @Valid @RequestBody CarRequest request);

    @Operation(summary = "Update an existing car", description = "Updates all fields of an existing car")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class),
                            examples = @ExampleObject(name = "updated", value = """
                                    {
                                      "id": 1,
                                      "vin": "1HGBH41JXMN109186",
                                      "brand": "Honda",
                                      "model": "Civic",
                                      "manufacturedYear": 2023,
                                      "color": "Blue",
                                      "fuelType": "GASOLINE",
                                      "transmission": "Automatic",
                                      "basePrice": 25000.00,
                                      "sellingPrice": 23500.00,
                                      "status": "RESERVED",
                                      "createdAt": "2024-01-01T10:00:00",
                                      "updatedAt": "2024-06-01T12:00:00"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "validation-error", value = """
                                    {
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "message": "One or more fields have validation errors",
                                      "fieldErrors": {
                                        "basePrice": "Base price is required"
                                      },
                                      "timestamp": "2024-01-01T10:00:00"
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "not-found", value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Car not found with id: 99",
                                      "fieldErrors": null,
                                      "timestamp": "2024-01-01T10:00:00"
                                    }"""))),
            @ApiResponse(responseCode = "409", description = "Duplicate VIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "duplicate-vin", value = """
                                    {
                                      "status": 409,
                                      "error": "Conflict",
                                      "message": "A car with VIN '1HGBH41JXMN109186' already exists",
                                      "fieldErrors": null,
                                      "timestamp": "2024-01-01T10:00:00"
                                    }""")))
    })
    ResponseEntity<CarResponse> update(
            @Parameter(description = "Car ID") @PathVariable Long id,
            @Valid @RequestBody CarRequest request);

    @Operation(summary = "Delete a car", description = "Deletes a car by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "not-found", value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Car not found with id: 99",
                                      "fieldErrors": null,
                                      "timestamp": "2024-01-01T10:00:00"
                                    }""")))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Car ID") @PathVariable Long id);
}
