package com.interview.autoshop;

import com.interview.autoshop.dto.AutoshopResponse;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    public AutoshopResponse getById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }
}
