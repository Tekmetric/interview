package com.interview.autoshop;

import com.interview.autoshop.dto.AutoshopResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autoshops")
@RequiredArgsConstructor
public class AutoshopController {

    private final AutoshopService service;
    private final AutoshopMapper mapper;

    @GetMapping
    public Page<AutoshopResponse> list(
            @ParameterObject @PageableDefault(size = 1000, sort = "id") Pageable pageable) {
        return service.findAll(pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public AutoshopResponse getById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }
}
