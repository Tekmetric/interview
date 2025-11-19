package com.interview.service.impl;

import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import com.interview.mapper.CarMakeMapper;
import com.interview.model.CarMake;
import com.interview.model.CarMakeNotFoundException;
import com.interview.repository.CarMakeRepository;
import com.interview.service.CarMakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CarMakeServiceImpl implements CarMakeService {

    private final CarMakeRepository repo;

    public CarMakeServiceImpl(CarMakeRepository repo) {
        this.repo = repo;
    }

    @Override
    public CarMakeDto create(CarMakeCreateDto dto) {
        log.info("Creating CarMake: {}", dto);
        try {
            CarMake entity = CarMakeMapper.fromCreateDto(dto);
            CarMake saved = repo.save(entity);
            log.info("Created CarMake with id={}", saved.getId());
            return CarMakeMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Constraint violation while creating CarMake: {}", dto, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public CarMakeDto getById(Long id) {
        log.info("Fetching CarMake by id={}", id);
        CarMake entity = repo.findById(id)
                .orElseThrow(() -> new CarMakeNotFoundException("Car make not found: " + id));
        return CarMakeMapper.toDto(entity);
    }

    @Override
    public Page<CarMakeDto> getAll(String nameFilter, Pageable pageable) {
        log.info("Listing CarMakes - filter='{}', pageable={}", nameFilter, pageable);
        Page<CarMake> page;

        if (nameFilter != null && !nameFilter.isEmpty()) {
            page = repo.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        return page.map(CarMakeMapper::toDto);
    }

    @Override
    public CarMakeDto update(Long id, CarMakeUpdateDto dto) {
        log.info("Updating CarMake id={} with payload: {}", id, dto);
        CarMake existing = repo.findById(id)
                .orElseThrow(() -> new CarMakeNotFoundException("Car make not found: " + id));

        CarMakeMapper.applyUpdateDto(dto, existing);

        try {
            CarMake saved = repo.save(existing);
            log.info("Updated CarMake id={}", saved.getId());
            return CarMakeMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Constraint violation while updating CarMake id={} with payload: {}", id, dto, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting CarMake id={}", id);
        CarMake existing = repo.findById(id)
                .orElseThrow(() -> new CarMakeNotFoundException("Car make not found: " + id));
        repo.delete(existing);
        log.info("Deleted CarMake id={}", id);
    }
}
