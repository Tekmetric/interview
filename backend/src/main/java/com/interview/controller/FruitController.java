package com.interview.controller;

import com.interview.model.FruitCreateRequest;
import com.interview.model.FruitPatchRequest;
import com.interview.model.FruitResponse;
import com.interview.service.FruitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fruits")
@Validated
public class FruitController {

    private final FruitService fruitService;

    @Operation(summary = "Get all fruits")
    @ApiResponse(responseCode = "200", description = "List of fruits returned successfully")
    @GetMapping
    public ResponseEntity<List<FruitResponse>> getAllFruits() {
        return ResponseEntity.ok(fruitService.getAllFruits());
    }

    @Operation(summary = "Get fruit by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fruit found and returned"),
        @ApiResponse(responseCode = "404", description = "Fruit not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FruitResponse> getFruitById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(fruitService.getFruitById(id));
    }

    @Operation(summary = "Create a new fruit")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fruit created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate fruit")
    })
    @PostMapping
    public ResponseEntity<FruitResponse> create(@Valid @RequestBody FruitCreateRequest request) {
        return ResponseEntity.ok(fruitService.create(request));
    }

    @Operation(summary = "Update a fruit by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fruit updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate fruit"),
        @ApiResponse(responseCode = "404", description = "Fruit not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FruitResponse> update(@PathVariable("id") Long id, @Valid @RequestBody FruitCreateRequest request) {
        return ResponseEntity.ok(fruitService.update(id, request));
    }

    @Operation(summary = "Patch a fruit by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fruit patched successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate fruit"),
        @ApiResponse(responseCode = "404", description = "Fruit not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FruitResponse> patch(@PathVariable("id") Long id, @RequestBody FruitPatchRequest request) {
        return ResponseEntity.ok(fruitService.patch(id, request));
    }

    @Operation(summary = "Delete a fruit by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Fruit deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Fruit not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        fruitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get fruits by supplier")
    @ApiResponse(responseCode = "200", description = "List of fruits for supplier returned successfully")
    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<List<FruitResponse>> getBySupplier(@PathVariable("supplier") String supplier) {
        return ResponseEntity.ok(fruitService.getFruitsBySupplier(supplier));
    }

    @Operation(summary = "Get fruits by supplier and batch number")
    @ApiResponse(responseCode = "200", description = "List of fruits for supplier and batch returned successfully")
    @GetMapping("/supplier/{supplier}/batch/{batchNumber}")
    public ResponseEntity<List<FruitResponse>> getByBatchNumberAndSupplier(
            @PathVariable("supplier") String supplier,
            @PathVariable("batchNumber") String batchNumber) {
        return ResponseEntity.ok(fruitService.getFruitsByBatchNumberAndSupplier(batchNumber, supplier));
    }
}
