package com.interview.controller;

import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.service.WishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public Page<WishLightDTO> getAllWishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return wishService.getAllWishes(pageable);
    }

    @GetMapping("/{id}")
    public WishDTO getWishById(@PathVariable Long id) {
        return wishService.getWishById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishDTO createWish(@Valid @RequestBody WishDTO wishDTO) {
        return wishService.createWish(wishDTO);
    }

    @PutMapping("/{id}")
    public WishDTO updateWish(@PathVariable Long id, @Valid @RequestBody WishDTO wishDTO) {
        return wishService.updateWish(id, wishDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWish(@PathVariable Long id) {
        wishService.deleteWish(id);
    }

    @PatchMapping("/{id}/came-true")
    public WishDTO markAsCameTrue(@PathVariable Long id) {
        return wishService.markAsCameTrue(id);
    }
}
