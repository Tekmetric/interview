package com.interview.controller;

import com.interview.api.CarApi;
import com.interview.dto.CarRequest;
import com.interview.dto.CarResponse;
import com.interview.model.CarStatus;
import com.interview.service.CarService;
import com.interview.validator.CarRequestValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/carshop/v1/cars")
@RequiredArgsConstructor
public class CarController implements CarApi {

    private final CarService carService;
    private final CarRequestValidator carRequestValidator;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarResponse> getById(@PathVariable Long id) {
        log.info("GET /carshop/v1/cars/{}", id);
        return ResponseEntity.ok(carService.getById(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CarResponse>> getAll(
            @RequestParam(required = false) CarStatus status,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("GET /carshop/v1/cars page={} pageSize={} status={} brand={}", page, pageSize, status, brand);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id"));
        return ResponseEntity.ok(carService.getAll(status, brand, minPrice, maxPrice, pageable));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarResponse> create(@Valid @RequestBody CarRequest request) {
        log.info("POST /carshop/v1/cars vin={}", request.vin());
        carRequestValidator.validate(request);
        CarResponse response = carService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarResponse> update(@PathVariable Long id, @Valid @RequestBody CarRequest request) {
        log.info("PUT /carshop/v1/cars/{} vin={}", id, request.vin());
        carRequestValidator.validate(request);
        return ResponseEntity.ok(carService.update(id, request));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /carshop/v1/cars/{}", id);
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
