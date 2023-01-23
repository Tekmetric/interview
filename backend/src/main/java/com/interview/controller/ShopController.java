package com.interview.controller;

import com.interview.dto.shop.CreateShopDto;
import com.interview.dto.shop.ShopDto;
import com.interview.dto.shop.UpdateShopDto;
import com.interview.service.ShopService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@Tag(name = "Shop Management Authenticated Rest API", description = "Defines authenticated endpoints for the shop management")
public class ShopController {

    private ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/shops")
    public ResponseEntity<ShopDto> createShop(
            @Valid @RequestBody CreateShopDto createShopDto
    ) {
        ShopDto shopDto = shopService.createShop(createShopDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(shopDto);
    }

    @GetMapping("/shops/{id}")
    public ResponseEntity<ShopDto> getShop(
            @PathVariable String id
    ) {
        UUID shopId = UUID.fromString(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(shopService.getShop(shopId));
    }

    @GetMapping("/shops")
    public ResponseEntity<Page<ShopDto>> listShops(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") @Max(20) int size,
            @RequestParam(defaultValue = "createdDate") String field,
            @RequestParam(defaultValue = "ASC") String order
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), field));
        Page<ShopDto> result = shopService.listShops(pageable, search.trim());
        return ResponseEntity.ok().body(result);
    }


    @PutMapping("/shops/{id}")
    public ResponseEntity<ShopDto> updateShop(
            @PathVariable String id,
            @Valid @RequestBody UpdateShopDto updateShopDto
    ) {
        UUID shopId = UUID.fromString(id);
        return ResponseEntity.ok().body(shopService.updateShop(shopId, updateShopDto));
    }

    @DeleteMapping("/shops/{id}")
    public ResponseEntity<Void> deleteShop(
            @PathVariable String id
    ) {
        UUID shopId = UUID.fromString(id);
        shopService.deleteShop(shopId);
        return ResponseEntity.noContent().build();
    }
}
