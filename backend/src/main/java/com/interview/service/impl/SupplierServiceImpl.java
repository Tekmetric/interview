package com.interview.service.impl;

import com.interview.domain.Supplier;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.SupplierRepository;
import com.interview.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Supplier findMostUsedSupplier() {
        var suppliersList = supplierRepository.findMostUsedSupplier();
        return suppliersList.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Couldn't find most used supplier"));
    }
}
