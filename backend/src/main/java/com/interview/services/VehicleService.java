package com.interview.services;

import com.interview.domain.Vehicle;
import com.interview.dtos.VehiclePatchDTO;
import com.interview.dtos.VehicleRequestDTO;
import com.interview.dtos.VehicleResponseDTO;
import com.interview.exceptions.ResourceAlreadyExistsException;
import com.interview.exceptions.ResourceNotFoundException;
import com.interview.mappers.VehicleMapper;
import com.interview.repositories.VehicleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository repository;
    private final VehicleMapper vehicleMapper;

    @Cacheable(cacheNames = "vehicles", key = "#id")
    public VehicleResponseDTO findById(@NotNull Long id) {
        return toDto(findByIdOrThrow(id));
    }

    public Page<VehicleResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(vehicleMapper::toDto);
    }

    @Transactional
    public VehicleResponseDTO create(@Valid VehicleRequestDTO vehicleDTO) {
        checkVinUnique(vehicleDTO.vin(), null);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        return toDto(repository.save(vehicle));
    }

    @Transactional
    @CacheEvict(cacheNames = "vehicles", key = "#id")
    public VehicleResponseDTO update(@NotNull Long id, @Valid VehicleRequestDTO vehicleDTO) {
        Vehicle existing = findByIdOrThrow(id);
        checkVinUnique(vehicleDTO.vin(), existing.getId());
        vehicleMapper.updateEntity(vehicleDTO, existing);
        return toDto(repository.save(existing));
    }

    @Transactional
    @CacheEvict(cacheNames = "vehicles", key = "#id")
    public VehicleResponseDTO patch(@NotNull Long id, VehiclePatchDTO vehicleDTO) {
        Vehicle existing = findByIdOrThrow(id);
        if (vehicleDTO.vin() != null) {
            checkVinUnique(vehicleDTO.vin(), existing.getId());
        }
        vehicleMapper.patchEntity(vehicleDTO, existing);
        return toDto(repository.save(existing));
    }

    @Transactional
    @CacheEvict(cacheNames = "vehicles", key = "#id")
    public void delete(@NotNull Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", id);
        }
        repository.deleteById(id);
    }

    @Cacheable(cacheNames = "vehicles", key = "#vin")
    public Optional<VehicleResponseDTO> findByVin(@NotNull String vin) {
        return repository.findByVin(vin).map(vehicleMapper::toDto);
    }

    private Vehicle findByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    private void checkVinUnique(String vin, Long currentVehicleId) throws ResourceAlreadyExistsException {
        boolean exists = repository.existsByVinAndIdNot(vin, currentVehicleId);
        if (exists) {
            throw new ResourceAlreadyExistsException("Vehicle with VIN already exists: %s".formatted(vin));
        }
    }

    private VehicleResponseDTO toDto(Vehicle vehicle) {
        return vehicleMapper.toDto(vehicle);
    }
}
