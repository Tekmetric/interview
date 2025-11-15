package com.interview.service;

import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarMakeService {

    CarMakeDto create(CarMakeCreateDto dto);

    CarMakeDto getById(Long id);

    Page<CarMakeDto> getAll(String nameFilter, Pageable pageable);

    CarMakeDto update(Long id, CarMakeUpdateDto dto);

    void delete(Long id);
}