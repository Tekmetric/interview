package com.interview.autoshop.services;

import com.interview.autoshop.dto.ServiceRequestDto;
import com.interview.autoshop.dto.create.CreateServiceRequestDto;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestService {

    Optional<ServiceRequestDto> findById(Long id);

    List<ServiceRequestDto> findAll(Boolean isOpen, String email);

    ServiceRequestDto create(CreateServiceRequestDto serviceRequestDto);

    void deleteById(Long id);

    ServiceRequestDto update(Long id, CreateServiceRequestDto serviceRequestDto);

    boolean isRequestPresent(Long id);
}
