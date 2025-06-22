package com.interview.resource;

import com.interview.domain.dto.ShopDto;
import com.interview.service.ShopService;
import com.interview.domain.mapper.ShopMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/shops")
public class ShopResource {

    private final ShopService shopService;
    private final ShopMapper shopMapper;

    public ShopResource(ShopService shopService, ShopMapper shopMapper) {
        this.shopService = shopService;
        this.shopMapper = shopMapper;
    }

    @GetMapping
    public Page<ShopDto> getAllShops(Pageable pageable) {
        return shopService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getShopById(@PathVariable Long id) {
        return shopService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShopDto> createShop(@Valid @RequestBody ShopDto shopDto) {
        var saved = shopService.save(shopDto);
        return ResponseEntity.created(URI.create("/api/shops/" + saved.id())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopDto> updateShop(
            @PathVariable Long id,
            @Valid @RequestBody ShopDto shopDto
    ) {
        return shopService.update(id, shopDto)
                .map(updated -> ResponseEntity.ok(shopMapper.toDto(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        return shopService.findById(id)
                .map(existing -> {
                    shopService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
