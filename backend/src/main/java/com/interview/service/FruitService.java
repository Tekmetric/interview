package com.interview.service;

import com.interview.exception.BadRequestException;
import com.interview.exception.NotFoundException;
import com.interview.mappers.FruitMapper;
import com.interview.model.Fruit;
import com.interview.model.FruitCreateRequest;
import com.interview.model.FruitPatchRequest;
import com.interview.model.FruitResponse;
import com.interview.repository.FruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FruitService {

    private final FruitRepository fruitRepository;
    private final FruitMapper fruitMapper;

    public List<FruitResponse> getAllFruits() {
        return fruitMapper.toResponse(fruitRepository.findAll());
    }

    public List<FruitResponse> getFruitsBySupplier(final String supplier) {
        return fruitMapper.toResponse(fruitRepository.findBySupplier(supplier));
    }

    public FruitResponse getFruitById(final Long id) {
        return fruitMapper.toResponse(findFruitByIdOrThrow(id));
    }

    @Transactional
    public FruitResponse create(final FruitCreateRequest request) {
        checkFruitExists(request.getName(), request.getSupplier(), request.getBatchNumber());
        var fruit = fruitMapper.toEntity(request);
        return fruitMapper.toResponse(fruitRepository.save(fruit));
    }

    @Transactional
    public FruitResponse update(final Long id, final FruitCreateRequest request) {
        checkFruitExists(request.getName(), request.getSupplier(), request.getBatchNumber());
        var fruit = findFruitByIdOrThrow(id);
        fruitMapper.updateEntity(request, fruit);
        return fruitMapper.toResponse(fruitRepository.save(fruit));
    }

    @Transactional
    public FruitResponse patch(final Long id, final FruitPatchRequest request) {
        var fruit = findFruitByIdOrThrow(id);
        checkFruitExistsOnPatching(request, fruit);
        fruitMapper.patchEntity(request, fruit);
        return fruitMapper.toResponse(fruitRepository.save(fruit));
    }

    @Transactional
    public void delete(Long id) {
        var fruit = findFruitByIdOrThrow(id);
        fruitRepository.delete(fruit);
    }

    public List<FruitResponse> getFruitsByBatchNumberAndSupplier(String batchNumber, String supplier) {
        return fruitMapper.toResponse(fruitRepository.findByBatchNumberAndSupplier(batchNumber, supplier));
    }

    private Fruit findFruitByIdOrThrow(final Long id) {
        return fruitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fruit not found with id: " + id));
    }

    private void checkFruitExists(String name, String supplier, String batchNumber) {
        var fruitExists = fruitRepository.existsByNameAndSupplierAndBatchNumber(
                name, supplier, batchNumber);
        if (fruitExists) {
            throw new BadRequestException(String.format(
                    "Fruit already exists with name: %s, supplier: %s, batch number: %s",
                    name, supplier, batchNumber)
            );
        }
    }

    private void checkFruitExistsOnPatching(final FruitPatchRequest request, final Fruit fruit) {
        var newName = request.getName() != null ? request.getName() : fruit.getName();
        var newSupplier = request.getSupplier() != null ? request.getSupplier() : fruit.getSupplier();
        var newBatchNumber = request.getBatchNumber() != null ? request.getBatchNumber() : fruit.getBatchNumber();

        boolean nameChanged = request.getName() != null;
        boolean supplierChanged = request.getSupplier() != null;
        boolean batchChanged = request.getBatchNumber() != null;

        if (nameChanged || supplierChanged || batchChanged) {
            checkFruitExists(newName, newSupplier, newBatchNumber);
        }
    }
}
