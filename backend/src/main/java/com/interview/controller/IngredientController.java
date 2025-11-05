package com.interview.controller;

import com.interview.model.Ingredient;
import com.interview.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredients", description = "Ingredient management API for commercial kitchen operations")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * GET /api/ingredients - Get all ingredients
     */
    @Operation(summary = "Get all ingredients", description = "Retrieves a list of all ingredients in the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of ingredients",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class)))
    })
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    /**
     * GET /api/ingredients/{id} - Get a single ingredient by ID
     */
    @Operation(summary = "Get ingredient by ID", description = "Retrieves a specific ingredient by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ingredient",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class))),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(
            @Parameter(description = "ID of the ingredient to retrieve", required = true)
            @PathVariable Long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    /**
     * POST /api/ingredients - Create a new ingredient
     */
    @Operation(summary = "Create new ingredient", description = "Adds a new ingredient to the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ingredient created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ingredient object to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Ingredient.class)))
            @RequestBody Ingredient ingredient) {
        Ingredient createdIngredient = ingredientService.createIngredient(ingredient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
    }

    /**
     * PUT /api/ingredients/{id} - Update an existing ingredient
     */
    @Operation(summary = "Update ingredient", description = "Updates an existing ingredient's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredient updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class))),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(
            @Parameter(description = "ID of the ingredient to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated ingredient information",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Ingredient.class)))
            @RequestBody Ingredient ingredientDetails) {
        Ingredient updatedIngredient = ingredientService.updateIngredient(id, ingredientDetails);
        return ResponseEntity.ok(updatedIngredient);
    }

    /**
     * DELETE /api/ingredients/{id} - Delete an ingredient
     */
    @Operation(summary = "Delete ingredient", description = "Removes an ingredient from the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteIngredient(
            @Parameter(description = "ID of the ingredient to delete", required = true)
            @PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ingredient deleted successfully");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/ingredients/category/{category} - Get ingredients by category
     */
    @Operation(summary = "Get ingredients by category", description = "Retrieves all ingredients in a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ingredients",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class)))
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Ingredient>> getIngredientsByCategory(
            @Parameter(description = "Category name to filter by", required = true, example = "Dairy")
            @PathVariable String category) {
        List<Ingredient> ingredients = ingredientService.getIngredientsByCategory(category);
        return ResponseEntity.ok(ingredients);
    }

    /**
     * GET /api/ingredients/supplier/{supplier} - Get ingredients by supplier
     */
    @Operation(summary = "Get ingredients by supplier", description = "Retrieves all ingredients from a specific supplier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ingredients",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class)))
    })
    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<List<Ingredient>> getIngredientsBySupplier(
            @Parameter(description = "Supplier name to filter by", required = true, example = "Local Farm Direct")
            @PathVariable String supplier) {
        List<Ingredient> ingredients = ingredientService.getIngredientsBySupplier(supplier);
        return ResponseEntity.ok(ingredients);
    }

    /**
     * GET /api/ingredients/search?name={name} - Search ingredients by name
     */
    @Operation(summary = "Search ingredients by name", description = "Searches for ingredients matching the provided name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching ingredients",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<Ingredient>> searchIngredients(
            @Parameter(description = "Name or partial name to search for", required = true, example = "tomato")
            @RequestParam String name) {
        List<Ingredient> ingredients = ingredientService.searchIngredientsByName(name);
        return ResponseEntity.ok(ingredients);
    }

    /**
     * GET /api/ingredients/low-stock - Get ingredients that are low in stock
     */
    @Operation(summary = "Get low stock ingredients", description = "Retrieves all ingredients where current quantity is at or below minimum stock level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock ingredients",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingredient.class)))
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<Ingredient>> getLowStockIngredients() {
        List<Ingredient> ingredients = ingredientService.getLowStockIngredients();
        return ResponseEntity.ok(ingredients);
    }
}
