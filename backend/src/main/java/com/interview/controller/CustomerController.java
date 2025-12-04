package com.interview.controller;

import com.interview.dto.CustomerCreateDto;
import com.interview.dto.CustomerDto;
import com.interview.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService svc;

    public CustomerController(CustomerService svc) { this.svc = svc; }

    @GetMapping
    public List<CustomerDto> listAll() {
        return svc.findAll();
    }

    // optional pageable endpoint:
    @GetMapping("/page")
    public Page<CustomerDto> listPage(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return svc.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public CustomerDto get(@PathVariable Long id) {
        return svc.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto create(@Valid @RequestBody CustomerCreateDto dto) {
        return svc.create(dto);
    }

    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable Long id, @Valid @RequestBody CustomerCreateDto dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        svc.delete(id);
    }
}