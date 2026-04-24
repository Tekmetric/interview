package com.interview.autoshop.controller;

import com.interview.autoshop.controller.dto.AutoshopResponse;
import com.interview.autoshop.controller.dto.CreateAutoshopRequest;
import com.interview.autoshop.controller.dto.UpdateAutoshopRequest;
import com.interview.autoshop.service.AutoshopService;
import com.interview.autoshop.service.domain.AutoshopMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/autoshops")
@RequiredArgsConstructor
public class AutoshopController {

    private final AutoshopService service;
    private final AutoshopMapper mapper;

    @Tag(name = "Read")
    @Operation(summary = "List autoshops.")
    @GetMapping
    public Page<AutoshopResponse> list(
            @ParameterObject @PageableDefault(size = 1000, sort = "id") Pageable pageable) {
        return service.findAll(pageable).map(mapper::toResponse);
    }

    @Tag(name = "Read")
    @Operation(summary = "Get an autoshop by id.")
    @GetMapping("/{id}")
    public AutoshopResponse getById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @Tag(name = "Create")
    @Operation(summary = "Create an autoshop.")
    @PostMapping
    public ResponseEntity<AutoshopResponse> create(@Valid @RequestBody CreateAutoshopRequest request) {
        AutoshopResponse created = mapper.toResponse(service.create(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Tag(name = "Update")
    @Operation(summary = "Replace an autoshop.")
    @PutMapping("/{id}")
    public AutoshopResponse replace(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAutoshopRequest request) {
        return mapper.toResponse(service.replace(id, request));
    }

    @Tag(name = "Delete")
    @Operation(summary = "Delete an autoshop.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
