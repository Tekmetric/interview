package com.interview.controller;

import com.interview.dto.SupplierDTO;
import com.interview.mapper.SupplierMapper;
import com.interview.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;

    @GetMapping
    public SupplierDTO getMostUsedSupplier() {
        return supplierMapper.modelToDto(supplierService.findMostUsedSupplier());
    }
}
