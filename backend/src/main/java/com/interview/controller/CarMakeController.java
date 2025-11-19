package com.interview.controller;

import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import com.interview.service.CarMakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/car-makes")
public class CarMakeController {

    private final CarMakeService service;

    public CarMakeController(CarMakeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CarMakeDto> create(@RequestBody CarMakeCreateDto dto) {
        log.info("POST /api/car-makes - payload: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarMakeDto> getOne(@PathVariable Long id) {
        log.info("GET /api/car-makes/{} - fetching", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CarMakeDto>> getAll(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        log.info("GET /api/car-makes - name={}, pageable={}", name, pageable);
        Page<CarMakeDto> result = service.getAll(name, pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarMakeDto> update(
            @PathVariable Long id,
            @RequestBody CarMakeUpdateDto dto
    ) {
        log.info("PUT /api/car-makes/{} - payload: {}", id, dto);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/car-makes/{} - deleting", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}