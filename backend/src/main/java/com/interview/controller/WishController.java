package com.interview.controller;

import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
@Tag(name = "Wish Management", description = "Endpoints for managing a wish list")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    @Operation(summary = "Get all wishes", description = "Retrieves a paginated list of wishes in a lightweight format")
    public Page<WishLightDTO> getAllWishes(
            @Parameter(description = "Zero-based page index (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "The size of the page to be returned") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return wishService.getAllWishes(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wish by ID", description = "Returns a single wish with full details")
    public WishDTO getWishById(@PathVariable Long id) {
        return wishService.getWishById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new wish", description = "Adds a new wish to the list")
    public WishDTO createWish(@Valid @RequestBody WishDTO wishDTO) {
        return wishService.createWish(wishDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a wish", description = "Updates an existing wish's details")
    public WishDTO updateWish(@PathVariable Long id, @Valid @RequestBody WishDTO wishDTO) {
        return wishService.updateWish(id, wishDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a wish", description = "Soft-deletes a wish by setting its deleted flag")
    public void deleteWish(@PathVariable Long id) {
        wishService.deleteWish(id);
    }

    @PatchMapping("/{id}/came-true")
    @Operation(summary = "Mark wish as came true", description = "Updates the status of a wish to reflect it has been fulfilled")
    public WishDTO markAsCameTrue(@PathVariable Long id) {
        return wishService.markAsCameTrue(id);
    }
}
