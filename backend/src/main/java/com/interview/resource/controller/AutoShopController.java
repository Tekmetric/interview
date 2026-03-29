package com.interview.resource.controller;

import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;
import com.interview.resource.service.IAutoShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/autoShops")
public class AutoShopController {

    private final IAutoShopService service;

    public AutoShopController(IAutoShopService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN' )")
    public ResponseEntity<AutoShopResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AutoShopResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutoShopResponse> create (@Valid @RequestBody AutoShopRequest autoShop) {
        return ResponseEntity.ok(service.create(autoShop));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutoShopResponse> update(@PathVariable Long id, @Valid @RequestBody AutoShopRequest autoShop) {
        return ResponseEntity.ok(service.update(id, autoShop));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
