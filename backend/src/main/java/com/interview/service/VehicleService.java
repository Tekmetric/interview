package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    static final int MAX_METADATA_KEY_LENGTH = 64;
    static final int MAX_METADATA_VALUE_LENGTH = 256;

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse findById(UUID id) {
        log.debug("Get vehicle by id={}", id);
        return vehicleRepository.findById(id)
                .map(VehicleService::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Page<VehicleResponse> findAll(String make, Integer year, String customerName, Pageable pageable) {
        String customer = blankToNull(customerName);
        log.debug(
                "List vehicles make={} year={} customerName={} page={} size={}",
                make,
                year,
                customer,
                pageable.getPageNumber(),
                pageable.getPageSize());
        return vehicleRepository.findAll(make, year, customer, pageable)
                .map(VehicleService::toResponse);
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        String vin = blankToNull(request.vin());
        if (hasVin(vin) && vehicleRepository.existsByVin(vin)) {
            throw new ResourceAlreadyExistsException("Vehicle", "vin", vin);
        }

        Vehicle entity = new Vehicle();
        entity.setId(UUID.randomUUID());
        applyRequest(entity, request, vin);
        Vehicle saved = vehicleRepository.save(entity);
        log.info("Created vehicle id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        String newVin = blankToNull(request.vin());
        if (existing.getVin() != null && newVin == null) {
            throw new IllegalArgumentException("Cannot remove VIN once set");
        }
        if (hasVin(newVin)
                && !Objects.equals(existing.getVin(), newVin)
                && vehicleRepository.existsByVin(newVin)) {
            throw new ResourceAlreadyExistsException("Vehicle", "vin", newVin);
        }

        applyRequest(existing, request, newVin);
        Vehicle saved = vehicleRepository.save(existing);
        log.info("Updated vehicle id={}", id);
        return toResponse(saved);
    }

    @Transactional
    public void deleteById(UUID id) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        vehicleRepository.delete(existing);
        log.info("Deleted vehicle id={}", id);
    }

    private static void applyRequest(Vehicle entity, VehicleRequest request, String normalizedVin) {
        entity.setMake(request.make());
        entity.setModel(request.model());
        entity.setYear(request.year());
        entity.setVin(normalizedVin);
        entity.setMileage(request.mileage());
        entity.setLicensePlate(blankToNull(request.licensePlate()));
        entity.setCustomerName(blankToNull(request.customerName()));
        entity.setFuelType(request.fuelType());
        entity.setMetadata(normalizeMetadata(request.metadata()));
    }

    private static VehicleResponse toResponse(Vehicle entity) {
        return new VehicleResponse(
                entity.getId(),
                entity.getMake(),
                entity.getModel(),
                entity.getYear(),
                entity.getVin(),
                entity.getMileage(),
                entity.getLicensePlate(),
                entity.getCustomerName(),
                entity.getFuelType(),
                entity.getMetadata(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static boolean hasVin(String vin) {
        return vin != null && !vin.isBlank();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    /**
     * Flat string map only; {@code null} or empty after validation clears stored metadata.
     */
    static Map<String, String> normalizeMetadata(Map<String, String> raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        Map<String, String> out = new LinkedHashMap<>();
        for (var e : raw.entrySet()) {
            if (e.getKey() == null) {
                throw new IllegalArgumentException("Metadata keys must not be null");
            }
            String key = e.getKey().trim();
            if (key.isEmpty()) {
                throw new IllegalArgumentException("Metadata keys must be non-blank");
            }
            if (key.length() > MAX_METADATA_KEY_LENGTH) {
                throw new IllegalArgumentException(
                        "Metadata keys must be at most " + MAX_METADATA_KEY_LENGTH + " characters");
            }
            if (e.getValue() == null) {
                throw new IllegalArgumentException("Metadata values must not be null");
            }
            String value = e.getValue();
            if (value.length() > MAX_METADATA_VALUE_LENGTH) {
                throw new IllegalArgumentException(
                        "Metadata values must be at most " + MAX_METADATA_VALUE_LENGTH + " characters");
            }
            out.put(key, value);
        }
        return out.isEmpty() ? null : Map.copyOf(out);
    }
}
