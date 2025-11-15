package com.interview.service;

import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import com.interview.mapper.CarMakeMapper;
import com.interview.model.CarMake;
import com.interview.model.CarMakeNotFoundException;
import com.interview.repository.CarMakeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarMakeService {

    private final CarMakeRepository repo;

    public CarMakeService(CarMakeRepository repo) {
        this.repo = repo;
    }

    public CarMakeDto create(CarMakeCreateDto dto) {
        try {
            CarMake entity = CarMakeMapper.fromCreateDto(dto);
            CarMake saved = repo.save(entity);
            return CarMakeMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Car make must be unique and valid.", ex);
        }
    }

    public CarMakeDto getById(Long id) {
        CarMake entity = repo.findById(id)
                .orElseThrow(() -> new CarMakeNotFoundException("Car make not found: " + id));
        return CarMakeMapper.toDto(entity);
    }

    public Page<CarMakeDto> getAll(String nameFilter, Pageable pageable) {
        Page<CarMake> page;

        if (nameFilter != null && !nameFilter.isEmpty()) {
            page = repo.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        return page.map(CarMakeMapper::toDto);
    }

    public CarMakeDto update(Long id, CarMakeUpdateDto dto) {
        CarMake existing = repo.findById(id)
                .orElseThrow(() -> new CarMakeNotFoundException("Car make not found: " + id));

        CarMakeMapper.applyUpdateDto(dto, existing);

        try {
            CarMake saved = repo.save(existing);
            return CarMakeMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Car make must be unique and valid.", ex);
        }
    }

    public void delete(Long id) {
        CarMake existing = repo.findById(id)
                .orElseThrow(() -> new CarMakeNotFoundException("Car make not found: " + id));
        repo.delete(existing);
    }
}